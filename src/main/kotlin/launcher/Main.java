package launcher;

public class Main {
//
//    static {
//        try {
//            StringBuilder builder = new StringBuilder();
//            File file = new File("A:\\Projects\\tools\\src\\main\\resources");
//            File fileOut = new File("A:\\Projects\\tools\\src\\main\\kotlin\\R.java");
//            if(!fileOut.exists()) {
//                build(file, builder);
//                Files.write(fileOut.toPath(), builder.toString().getBytes(StandardCharsets.UTF_8));
//            }
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//    }

    public static void main(String[] args) {
        Application.launch(Application.class);
    }
}
