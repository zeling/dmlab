import java.util.HashMap;
import java.util.function.Supplier;

/**
 * Created by zeling on 16/5/14.
 *
 * @author zeling
 */
public class PropositionParser {

    private String source;
    private int pos;
    private boolean ready;

    private static HashMap<String, String> map = new HashMap<>();
    static {
        map.put("\\and", "∧");
        map.put("\\or", "∨");
        map.put("\\imply", "→");
        map.put("\\eq", "↔");
        map.put("\\not", "¬");
    }

    @Deprecated
    public PropositionParser(String source) {
        this();
        this.source = source;
        this.ready = true;
    }

    public PropositionParser() {
        this.pos = 0;
        this.ready = false;
    }

    public void reset(String s) {
        source = s;
        pos = 0;
        this.ready = true;
    }

    private char peek() {
        return source.charAt(pos);
    }

    private void skipWhiteSpace() {
        while (pos < source.length() && startsWithWhiteSpace()) pos++;
    }

    private boolean startsWith(String s) {
        return source.startsWith(s, pos);
    }

    private void should(Supplier<Boolean> pred, String expect) throws Exception{
        skipWhiteSpace();
        if (!pred.get()) {
            throw new Exception(String.format("At pos %d: found \'%c\' but expected \'%s\'", pos, peek(), expect));
        }
    }

    private void expect(String expect) throws Exception {
        should(() -> startsWith(expect), expect);
        pos += expect.length();
    }

    private boolean startsWithWhiteSpace() {
        return (peek() == ' ' || peek() == '\n' || peek() == '\t' || peek() == '\r');
    }

    private boolean startsWithLetter() {
        return peek() >= 'A' && peek() <= 'Z' || peek() >= 'a' && peek() <= 'z';
    }

    private boolean startsWithDigit() {
        return peek() >= '0' && peek() <= '9';
    }

    private Proposition.Letter parsePropLetter() throws Exception {
        should(this::startsWithLetter, "letters");
        int idStart;
        for (idStart = pos; pos < source.length() && startsWithLetter(); pos++) ;
        String letter = source.substring(idStart, pos);
        String sub = null;
        if (pos < source.length() && peek() == '_') {
            pos++;
            if (startsWithWhiteSpace()) {
                throw new Exception("Non-blank characters expected");
            }
            expect("{");
            sub = parseInteger();
            expect("}");
        }
        return new Proposition.Letter(letter, sub);
    }

    private String parseInteger() throws Exception {
        should(this::startsWithDigit, "digits");
        int dStart = pos;
        for (; startsWithDigit(); pos++) ;
        return source.substring(dStart, pos);
    }

    private Proposition tryParseConnective(Proposition lhs, String... connectives) throws Exception {
        skipWhiteSpace();
        for (String connective : connectives) {
            if (startsWith(connective)) {
                pos += connective.length();
                return new Proposition(map.get(connective), lhs, parse(false));
            }
        }
        throw new Exception("No connective recognized");
    }

    private Proposition parse(boolean toplevel) throws Exception {
        if (!ready) throw new Exception("The parser is not at a ready state");
        Proposition ret;
        skipWhiteSpace();
        if (peek() == '(') {
            pos++;
            if (peek() == '\\') {
                expect("\\not");
                ret = new Proposition(map.get("\\not"), parse(false));
            } else {
                ret = parse(false);
                ret = tryParseConnective(ret, "\\and", "\\or", "\\imply", "\\eq");
            }
            expect(")");
        } else {
            should(this::startsWithLetter, "letter");
            ret = new Proposition(parsePropLetter());
        }
        if (toplevel) should(() -> pos == source.length(), "Premature terminate or redundant characters at the end of the input");
        return ret;
    }

    public Proposition parse() throws Exception {
        return parse(true);
    }

    public Proposition parse(String string) throws Exception {
        reset(string);
        return parse();
    }
}