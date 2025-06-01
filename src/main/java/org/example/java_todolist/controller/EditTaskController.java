//package org.example.java_todolist.controller;
//
//import javafx.fxml.FXML;
//import javafx.scene.control.*;
//import javafx.stage.Stage;
//import org.example.java_todolist.model.Task;
//import org.example.java_todolist.database.Database;
//
//public class EditTaskController {
//
//    @FXML private TextField titleField;
//    @FXML private ComboBox<String> categoryComboBox; // Ubah menjadi ComboBox
//    @FXML private TextArea descriptionField;
//    @FXML private DatePicker deadlinePicker;
//    @FXML private CheckBox isDoneCheckBox;
//
//    private Task task;
//
//    public void setTask(Task task) {
//        this.task = task;
//        titleField.setText(task.getTitle());
//        categoryComboBox.setValue(task.getCategory()); // Set kategori yang sudah ada ke ComboBox
//        descriptionField.setText(task.getDescription());
//        deadlinePicker.setValue(java.time.LocalDate.parse(task.getDeadline()));
//        isDoneCheckBox.setSelected(task.getIsDone());
//    }
//
//    @FXML
//    private void initialize() {
//        // Menambahkan pilihan kategori pada ComboBox (sama seperti di AddTaskController)
//        categoryComboBox.getItems().addAll("Pekerjaan", "Pribadi", "Olahraga", "Belanja", "Lainnya");
//    }
//
//    @FXML
//    private void handleSaveChanges() {
//        try {
//            // Pastikan bahwa task.getId() adalah string yang valid
//            String idStr = task.getId();
//            if (idStr != null && !idStr.isEmpty()) {
//                int id = Integer.parseInt(idStr);
//
//                // Ambil nilai status dari CheckBox dan kategori dari ComboBox
//                boolean isDone = isDoneCheckBox.isSelected();
//                String category = categoryComboBox.getValue(); // Ambil kategori yang dipilih
//
//                // Panggil metode updateTask() dengan lima parameter
//                Database.updateTask(id, titleField.getText(), descriptionField.getText(), deadlinePicker.getValue().toString(), isDone, category);
//
//            } else {
//                throw new IllegalArgumentException("ID Task tidak valid!");
//            }
//        } catch (NumberFormatException e) {
//            System.out.println("ID Task tidak valid!");
//            e.printStackTrace();
//        } catch (IllegalArgumentException e) {
//            System.out.println(e.getMessage());
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        // Tutup jendela setelah menyimpan
//        Stage stage = (Stage) titleField.getScene().getWindow();
//        stage.close();
//    }
//
//    @FXML
//    private void handleCancel() {
//        Stage stage = (Stage) titleField.getScene().getWindow();
//        stage.close();
//    }
//}
