import java.util.Arrays;
import java.util.List;

public class DirectorySize {

    public long calculateSize(FileNode node) {
        if (node.isFile()) {
            return node.getSize();
        }

        long totalSize = 0;
        for (FileNode child : node.getChildren()) {
            // BUG: calculateSize(node) zamiast calculateSize(child)!
            totalSize += calculateSize(child);
        }
        return totalSize;
    }

    public static void main(String[] args) {
        // Drzewo katalogów:
        // home/
        //   documents/
        //     raport.pdf (1500)
        //     notatki.txt (200)
        //   photos/
        //     wakacje.jpg (3500)
        //     rodzina.jpg (2800)
        //   readme.txt (100)

        FileNode home = new FileNode("home", 0, Arrays.asList(
                new FileNode("documents", 0, Arrays.asList(
                        new FileNode("raport.pdf", 1500, null),
                        new FileNode("notatki.txt", 200, null)
                )),
                new FileNode("photos", 0, Arrays.asList(
                        new FileNode("wakacje.jpg", 3500, null),
                        new FileNode("rodzina.jpg", 2800, null)
                )),
                new FileNode("readme.txt", 100, null)
        ));

        DirectorySize calculator = new DirectorySize();
        // Oczekiwany rozmiar: 1500 + 200 + 3500 + 2800 + 100 = 8100
        long size = calculator.calculateSize(home);
        System.out.println("Rozmiar katalogu home: " + size + " bajtów");
        System.out.println("Oczekiwano: 8100 bajtów");
    }

    static class FileNode {
        private String name;
        private long size;
        private List<FileNode> children;

        FileNode(String name, long size, List<FileNode> children) {
            this.name = name;
            this.size = size;
            this.children = children;
        }

        String getName() { return name; }
        long getSize() { return size; }
        List<FileNode> getChildren() { return children; }

        boolean isFile() {
            return children == null;
        }
    }
    /*
1. Co zobaczysz w panelu Frames po `StackOverflowError`? Ile ramek `calculateSize`? niekonczonca sie ilosc ramek
2. Czy parametr `node` zmienia się między kolejnymi ramkami wywołań? (Kliknij na różne ramki i porównaj) wszystko dzieje sie z home
3. Na czym polega bug? Dlaczego rekurencja nigdy się nie kończy?
poniewaz petla sie nigdy nie konczy gdzyz sprawdzamy pierwszy node ktory zawsze ma home subchild i zwracamy number child dla tego samego pliku w kolko.
4. Jak naprawić bug? Napisz poprawną linię kodu. totalSize += calculateSize(child);
5. Jaki jest oczekiwany wynik po naprawie? (Policz ręcznie)
Rozmiar katalogu home: 8100 bajtów
Oczekiwano: 8100 bajtów
     */
}
