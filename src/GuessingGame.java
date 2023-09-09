import javax.swing.*;
import java.io.*;

public class GuessingGame implements java.io.Serializable {

    String[] menuOptions = {"Play", "Save Tree", "Load Tree", "Quit"};

    GuessingGame() {
        BinaryTree guessingTree;
        guessingTree = (BinaryTree) loadTreeFromFile("trees\\tree.ser");
        if (guessingTree == null) {
            guessingTree = generateInitialTree();
            saveTreeToFile("trees\\tree.ser", guessingTree);
        }
        int selectedAction;
        while (true) {
            selectedAction = JOptionPane.showOptionDialog(null, "Select an option", "Guessing Game",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, menuOptions, menuOptions);
            if (selectedAction == 0) {
                playGame(guessingTree);
            }
            if (selectedAction == 1)
                saveTreeToFile("trees\\tree.ser", guessingTree);
            if (selectedAction == 2)
                guessingTree = (BinaryTree) loadTreeFromFile("trees\\tree.ser");
            if (selectedAction == 3 || selectedAction == JOptionPane.CLOSED_OPTION)
                System.exit(-1);
        }
    }

    public void playGame(BinaryTree guessingTree) {
        BinaryNodeInterface prevNode = null;
        BinaryNodeInterface currentNode = guessingTree.root();
        Boolean prevNodeAnswer = null;

        while (currentNode != null) {
            Boolean CurrentAnswer = askQuestion(currentNode, null);
            Boolean leafNode = currentNode.isLeaf();

            if (leafNode) {
                if (CurrentAnswer) {
                    break;
                } else {
                    String newAnswer = getUserAnswer();
                    String newQuestion = getUserQuestion(prevNode, newAnswer);
                    BinaryTree newGuessingTree = new BinaryTree(newQuestion);

                    if (askQuestion(newGuessingTree.root(), newAnswer)) {
                        newGuessingTree.root().setLeftChild(new BinaryTree("Are you thinking of " + newAnswer + "?").root());
                        newGuessingTree.root().setRightChild(currentNode);
                    } else {
                        newGuessingTree.root().setLeftChild(currentNode);
                        newGuessingTree.root().setRightChild(new BinaryTree("Are you thinking of " + newAnswer + "?").root());
                    }

                    if (prevNodeAnswer == null) {
                        guessingTree.setRootNode(newGuessingTree.root());
                    } else if (prevNodeAnswer) {
                        prevNode.setLeftChild(newGuessingTree.root());
                    } else {
                        prevNode.setRightChild(newGuessingTree.root());
                    }

                    break;
                }
            } else {
                prevNode = currentNode;
                currentNode = CurrentAnswer ? currentNode.getLeftChild() : currentNode.getRightChild();
                prevNodeAnswer = CurrentAnswer;
            }
        }

        if (currentNode == null) {
            JOptionPane.showMessageDialog(null, "Sorry, I do not have an answer for that! ");
            String newAnswer = getUserAnswer();
            String newQuestion = getUserQuestion(prevNode, newAnswer);
            BinaryTree newGuessingTree = new BinaryTree(newQuestion);

            if (prevNodeAnswer) {
                prevNode.setLeftChild(newGuessingTree.root());
            } else {
                prevNode.setRightChild(newGuessingTree.root());
            }
        }
    }

    public BinaryTree<String> generateInitialTree() {
        BinaryTree<String> penguinQuestion = new BinaryTree<>("Is it a Penguin?");
        BinaryTree<String> canFlyQuestion = new BinaryTree<>("Can it fly?", null, penguinQuestion);
        BinaryTree<String> isBirdQuestion = new BinaryTree<>("Is it a bird?", canFlyQuestion, null);
        BinaryTree<String> isMammalQuestion = new BinaryTree<>("Is it a mammal?", null, isBirdQuestion);
        return new BinaryTree<>("Is it an animal?", isMammalQuestion, null);
    }

    public void saveTreeToFile(String saveFilePath, Object objectToSave) {
        File folder = new File("trees");
        if (!folder.exists()) {
            folder.mkdirs();
        }
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(saveFilePath))) {
            out.writeObject(objectToSave);
        } catch (IOException ex) {
            System.err.println("An IOException has been handled/encountered " + ex.getMessage());
        }
    }

    public Object loadTreeFromFile(String saveFilePath) {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(saveFilePath))) {
            return in.readObject();
        } catch (IOException | ClassNotFoundException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }

    public boolean askQuestion(BinaryNodeInterface node, String CurrentAnswer) {
        String questionToAsk = node.getData().toString();
        int userReply = JOptionPane.showConfirmDialog(null, CurrentAnswer == null ? questionToAsk : "Answer this question regarding " +CurrentAnswer + ".\n" + questionToAsk, "Guessing Game", JOptionPane.YES_NO_OPTION);
        return userReply == JOptionPane.YES_OPTION;
    }

    public String getUserAnswer() {
        String userReply = JOptionPane.showInputDialog(null, "What are you thinking about? input \"a\" or \"an\" ");
        if (userReply == null) {
            System.exit(-1);
        }
        return userReply;
    }

    public String getUserQuestion(BinaryNodeInterface prevNode, String newAnswer) {
        String prevNodeQuestion = prevNode.getData().toString();
        String userReply = JOptionPane.showInputDialog(null, "Write a Distinguishing question about " + newAnswer + " ?\n previous question: " + prevNodeQuestion);
        if (userReply == null) {
            System.exit(-1);
        }
        return userReply;
    }

    public void MethodForTesting(BinaryTree tree) {
        tree.inorderTraverse();
    }
}