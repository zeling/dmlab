package me.zeling.parser.earley;

import java.util.ArrayList;
import java.util.function.Function;

/**
 * Created by zeling on 16/5/11.
 *
 * @author zeling
 */
public class ParseTree<Token> {
    ArrayList<Either<Token, ParseTree<Token>>> children;

    public ParseTree() {
        children = new ArrayList<>();
    }

    public void appendLeaf(Token token) {
        children.add(new Left<>(token));
    }

    public void appendLeaf(ParseTree<Token> tree) {
        children.add(new Right<>(tree));
    }

}

class Either<A, B> {
    public A left() { throw new UnsupportedOperationException(); }
    public B right() { throw new UnsupportedOperationException(); }
    public <C> C either(Function<A, C> l, Function<B, C> r) {
        if (this instanceof Left) {
            return l.apply(left());
        } else {
            return r.apply(right());
        }
    }
}

class Left<A, B> extends Either<A, B> {
    private A left;
    public Left(A left) { this.left = left; }
    @Override public A left() { return left; }
}

class Right<A, B> extends Either<A, B> {
    private B right;
    public Right(B right) { this.right = right; }
    @Override public B right() { return right; }
}
