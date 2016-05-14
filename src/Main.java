import java.util.Scanner;

/**
 * Created by zeling on 16/5/14.
 *
 * @author zeling
 */
public class Main {
    public static void main(String[] args) throws Exception {
        Scanner s = new Scanner(System.in);
        PropositionParser parser = new PropositionParser();
        while (s.hasNext()) {
            parser.reset(s.nextLine());
            try {
                System.out.println(parser.parse());
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }
}


