package me.zeling.parser.earley;

import me.zeling.parser.grammar.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by zeling on 16/5/2.
 *
 * @author zeling
 */
public class Earley<Token extends Terminal> {
    ArrayList<ParseState> states;
    HashSet<NonTerminal> nullables;

    Grammar g;
    int currState;
    private boolean recognized = false;

    public Earley(Grammar g) {
        this.g = g;
        this.currState = 0;
        this.states = new ArrayList<>();
        ParseState initial = new ParseState();
        for (Production p : g.getGrammarMap().get(g.getStart())) {
            initial.add(new EarleyEntry(g.getStart(), new DottedProduction(p), 0));
        }
        states.add(initial);
        nullables = new HashSet<>();
        int size;
        do {

            size = nullables.size();
            for (Map.Entry<NonTerminal, HashSet<Production>> e : g.getGrammarMap().entrySet()) {
                for (Production p : e.getValue()) {
                    if (p.nullable() ||
                            (p.size() == 1 && p.get(0) instanceof NonTerminal && nullables.contains(p.get(0)))) {
                        nullables.add(e.getKey());
                    }
                }
            }

        } while (size != nullables.size());
    }

    private ParseState getNextState() {
        while (currState > states.size() - 2) {
            states.add(new ParseState());
        }
        return states.get(currState + 1);
    }

    private ParseState getCurrState() {
        while (currState > states.size() - 1) {
            states.add(new ParseState());
        }
        return states.get(currState);
    }

    public boolean recognize(Iterable<Token> tokens) {

        for (Token token : tokens) {

            ParseState st = getCurrState();

            for (int i = 0; i < st.size(); i++) {
                EarleyEntry entry = st.get(i);
                DottedProduction p = entry.production;
                if (p.isCompleted()) {
                    /* completed */
                    ParseState parent = states.get(entry.start);
                    for (int j = 0; j < parent.size(); j++) {
                        EarleyEntry pentry = parent.get(j);
                        if (!pentry.production.isCompleted()
                                && pentry.production.peekTermAfterDot().equals(entry.head)) {
                            st.add(new EarleyEntry(pentry.head, pentry.production.advanceDot(), pentry.start));

                        }
                    }
                } else if (p.peekTermAfterDot() instanceof NonTerminal) {
                    /* predict */
                    NonTerminal nonTerminal = (NonTerminal) p.peekTermAfterDot();
                    for (Production production : g.getGrammarMap().get(nonTerminal)) {
                        st.add(new EarleyEntry(nonTerminal, production, currState));
                    }
                    if (nullables.contains(nonTerminal)) {
                        st.add(new EarleyEntry(nonTerminal, p.advanceDot(), entry.start));
                    }
                } else if (token.equals(p.peekTermAfterDot())) {
                    /* scan */
                    ParseState st1 = getNextState();
                    st1.add(new EarleyEntry(entry.head, entry.production.advanceDot(), entry.start));
                }
            }

            currState++;
        }

        for (EarleyEntry entry : states.get(states.size() - 1)) {
            if (entry.start == 0 && entry.production.isCompleted() && entry.head.equals(g.getStart())) {
                return recognized = true;
            }
        }

        return recognized = false;
    }

}

//    public ParseTree parse() throws Exception {
//        if (!recognized) throw new IllegalStateException("Unexpected State");
//        if (graph == null) graph = new ArrayList<>(states.size());
//        for (int i = 0; i < graph.size(); i++) {
//            graph.set(i, new HashMap<>());
//        }
//        for (int i = 0; i < states.size(); i++) {
//            ParseState st = states.get(i);
//            for (int j = 0; j < st.size(); j++) {
//                EarleyEntry e = st.get(j);
//                HashMap<NonTerminal, HashSet<Edge>> map = graph.get(e.start);
//                map.putIfAbsent(e.head, new HashSet<>());
//                map.get(e.head).add(new Edge(i, e.production.getProduction()));
//            }
//        }
//
//        /* perform the depth-first search */
//        ParseTree parseTree = new ParseTree();
//
//        NonTerminal nt = g.getStart();
//        for (int i = 0; i < states.size(); i++) {
//            for (Edge edge : graph.get(i).get(nt)) {
//                if (edge.end == states.size() - 1) return parseTree;
//
//            }
//        }
//
//    }
//
//    /* precondition: graph has been successfully built */
//    private ParseTree search(int start, NonTerminal nt) {
//        ParseTree res = null;
//        if (graph == null) throw new IllegalStateException("Graph unconstructed");
//        for (Edge edge : graph.get(start).get(nt)) {
//            if (edge.end == end) {
//                res = new ParseTree();
//                for (Term term : edge.production) {
//                    if (term instanceof NonTerminal) {
//
//                    } else {
//
//                    }
//                }
//                return res;
//            }
//        }
//        return res;
//    }
//
//}
//
//class Edge {
//    int end;
//    Production production;
//
//    public Edge(int end, Production production) {
//        this.end = end;
//        this.production = production;
//    }
//}
