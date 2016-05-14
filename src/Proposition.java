import java.util.function.Function;

/**
 * Created by zeling on 16/5/14.
 *
 * @author zeling
 */
public class Proposition {

    public static class Unary<T> {
        T child;

        public Unary(T child) {
            this.child = child;
        }

        public T getChild() {
            return child;
        }
    }

    public static class Binary<T> {
        T left;
        T right;

        public Binary(T left, T right) {
            this.left = left;
            this.right = right;
        }

        public T getLeft() {
            return left;
        }

        public T getRight() {
            return right;
        }
    }

    public static class Letter {
        String letter;
        String sub;

        public Letter(String letter, String sub) {
            this.letter = letter;
            this.sub = sub;
        }

        @Override
        public String toString() {
            if (sub == null) {
                return letter;
            } else {
                StringBuilder sb = new StringBuilder(letter);
                for (int i = 0; i < sub.length(); i++) {
                    sb.append((char) (sub.charAt(i) - '0' + '\u2080'));
                }
                return sb.toString();
            }
        }
    }

    public static void main(String[] args) {
        System.out.println(new Proposition("\\and", new Proposition("\\not", new Proposition(new Letter("A", "1"))), new Proposition(new Letter("B", "1"))));
    }

    private Either<Letter, Either<Unary<Proposition>, Binary<Proposition>>> proxy;
    private String stringLit;
    private String repr;

    /* don't touch my private parts */
    private Proposition(Either<Letter, Either<Unary<Proposition>, Binary<Proposition>>> proxy) {
        this.proxy = proxy;
    }

    public Proposition(Letter letter) {
        this(new Either.Left<>(letter));
    }

    public Proposition(String connective, Proposition child) {
        this(new Either.Right<>(new Either.Left<>(new Unary<>(child))));
        this.stringLit = connective;
    }

    public Proposition(String connective, Proposition lchild, Proposition rchild) {
        this(new Either.Right<>(new Either.Right<>(new Binary<>(lchild, rchild))));
        this.stringLit = connective;
    }

    public <C> C apply (Function<Letter, C> letter, Function<Unary<Proposition>, C> unary, Function<Binary<Proposition>, C> binary) {
        return proxy.either(letter, p -> p.either(unary, binary));
    }

    @Override
    public String toString() {
        if (repr != null) return repr;
        return repr = proxy.either(Object::toString, p -> p.either(u -> "(" + stringLit + " " + u.child.toString() + ")",
                b -> "(" + b.left.toString() + " " + stringLit + " " + b.right.toString() + ")"));
    }
}


