import java.util.function.Function;

/**
 * Created by zeling on 16/5/14.
 *
 * @author zeling
 */

/* JAVA's got not UnionType which can be pattern matched on so I fucking invented one */
public class Either<A, B> {

    public static class Left<A, B> extends Either<A, B> {
        private A left;

        public Left(A left) {
            this.left = left;
        }

        public A left() {
            return left;
        }
    }

    public static class Right<A, B> extends Either <A, B> {
        private B right;

        public Right(B right) {
            this.right = right;
        }

        public B right() {
            return right;
        }
    }

    public A left() {
        throw new UnsupportedOperationException();
    }
    public B right() {
        throw new UnsupportedOperationException();
    }

    public <C> C either(Function<A, C> l, Function<B, C> r) {
        if (this instanceof Left) {
            return l.apply(this.left());
        } else if (this instanceof Right) {
            return r.apply(this.right());
        }
        throw new UnsupportedOperationException();
    }

    @Override
    public String toString() {
        return either(Object::toString, Object::toString);
    }
}


