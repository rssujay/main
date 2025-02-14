package integration;

import org.junit.jupiter.api.Test;
import spinbox.DateTime;
import spinbox.Parser;
import spinbox.Ui;
import spinbox.commands.Command;
import spinbox.containers.ModuleContainer;
import spinbox.containers.lists.TaskList;
import spinbox.entities.Module;
import spinbox.entities.items.tasks.Deadline;
import spinbox.entities.items.tasks.Event;
import spinbox.entities.items.tasks.Exam;
import spinbox.entities.items.tasks.Lab;
import spinbox.entities.items.tasks.Lecture;
import spinbox.entities.items.tasks.Task;
import spinbox.entities.items.tasks.Todo;
import spinbox.entities.items.tasks.Tutorial;

import spinbox.exceptions.DataReadWriteException;
import spinbox.exceptions.InputException;
import spinbox.exceptions.InvalidIndexException;
import spinbox.exceptions.SpinBoxException;
import spinbox.exceptions.FileCreationException;
import spinbox.exceptions.CorruptedDataException;
import spinbox.exceptions.DateFormatException;
import spinbox.exceptions.ScheduleDateException;
import spinbox.exceptions.StorageException;

import java.util.ArrayDeque;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class TaskCommandsIntegrationTest {
    private ModuleContainer testContainer;
    private Module testModule;
    private TaskList taskList;
    private ArrayDeque<String> pageTrace;
    private Command command;
    private Ui ui;

    /**
     * Clears the taskList of any tasks that are stored from previous tests.
     * @throws InvalidIndexException If an invalid index is entered.
     * @throws DataReadWriteException If there is an error reading/writing to the files.
     */
    private void clearTaskList() throws InvalidIndexException, DataReadWriteException {
        while (taskList.size() > 0) {
            taskList.remove(0);
        }
    }

    /**
     * Initialize the set up which creates a test module and adds the test module into the module container.
     * @throws FileCreationException If there is an error when creating new files.
     * @throws DataReadWriteException If there is an error reading/writing to the files.
     * @throws CorruptedDataException If the data within the files have been corrupted.
     * @throws DateFormatException If an invalid date format is provided.
     */
    private void initializeSetUp() throws FileCreationException, DataReadWriteException, CorruptedDataException,
            DateFormatException {
        testContainer = new ModuleContainer();
        testModule = new Module("TESTMOD", "Test Module");
        testContainer.addModule(testModule);
        pageTrace = new ArrayDeque<>();
        ui = new Ui(true);
    }

    /**
     * Executes the command that is provided to the Parser.
     * @param userInput The String input provided to the test.
     * @throws SpinBoxException If there are storage errors or input errors.
     */
    private void executeCommand(String userInput) throws SpinBoxException {
        Parser.setPageTrace(pageTrace);
        command = Parser.parse(userInput);
        command.execute(testContainer, pageTrace, ui, false);
    }

    @Test
    public void loadDataSuccessful_AddTasksThenManualClear_successfulRepopulationOfData() throws
            StorageException, InvalidIndexException, ScheduleDateException, DateFormatException {
        Module testModuleOne = new Module("testMod6", "Engineering Principles & Practice IV");

        taskList = testModuleOne.getTasks();
        clearTaskList();
        Task testTask1 = new Todo("Test 1");
        Task testTask2 = new Deadline("Test 2", new DateTime("01/01/2029 10:00"));
        Task testTask3 = new Event("Test 3", new DateTime("01/02/2029 10:00"),
                new DateTime("01/02/2029 12:00"));
        Task testTask4 = new Exam("Test 4", new DateTime("01/03/2029 10:00"),
                new DateTime("01/03/2029 12:00"));
        Task testTask5 = new Lab("Test 5", new DateTime("01/04/2029 10:00"),
                new DateTime("01/04/2029 12:00"));
        Task testTask6 = new Lecture("Test 6", new DateTime("01/05/2029 10:00"),
                new DateTime("01/05/2029 12:00"));
        Task testTask7 = new Tutorial("Test 7", new DateTime("01/06/2029 10:00"),
                new DateTime("01/06/2029 12:00"));

        taskList.add(testTask1);
        taskList.add(testTask2);
        taskList.add(testTask3);
        taskList.add(testTask4);
        taskList.add(testTask5);
        taskList.add(testTask6);
        taskList.add(testTask7);
        taskList.clear();
        taskList.loadData();

        assertEquals(taskList.get(0).toString(), "[D][NOT DONE] Test 2 (by: 01/01/2029 10:00)");
        assertEquals(taskList.get(1).toString(), "[E][NOT DONE] Test 3 (at: 01/02/2029 10:00"
                + " to 01/02/2029 12:00)");
        assertEquals(taskList.get(2).toString(), "[EXAM][NOT DONE] Test 4 (at: 01/03/2029 10:00"
                + " to 01/03/2029 12:00)");
        assertEquals(taskList.get(3).toString(), "[LAB][NOT DONE] Test 5 (at: 01/04/2029 10:00"
                + " to 01/04/2029 12:00)");
        assertEquals(taskList.get(4).toString(), "[LEC][NOT DONE] Test 6 (at: 01/05/2029 10:00"
                + " to 01/05/2029 12:00)");
        assertEquals(taskList.get(5).toString(), "[TUT][NOT DONE] Test 7 (at: 01/06/2029 10:00"
                + " to 01/06/2029 12:00)");
        assertEquals(taskList.get(6).toString(), "[T][NOT DONE] Test 1");
    }

    @Test
    public void setNameSuccessful_setNameOfTaskToANewName_taskNameSuccessfullySet() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Todo("Test 1");
        Task testTask2 = new Lab("Test 2", new DateTime("01/04/2029 10:00"),
                new DateTime("01/04/2029 12:00"));
        Task testTask3 = new Tutorial("Test 3", new DateTime("01/05/2030 10:00"),
                new DateTime("01/05/2030 12:00"));
        Task testTask4 = new Event("Test 4", new DateTime("01/05/2031 10:00"),
                new DateTime("01/05/2031 12:00"));
        Task testTask5 = new Exam("Test 5", new DateTime("01/03/2032 10:00"),
                new DateTime("01/03/2032 12:00"));

        taskList.add(testTask1);
        taskList.add(testTask2);
        taskList.add(testTask3);
        taskList.add(testTask4);
        taskList.add(testTask5);

        pageTrace.add("main");

        String setNameForTask1 = "set-name TESTMOD / task 1 to: Lab";
        executeCommand(setNameForTask1);

        String setNameForTask3 = "set-name TESTMOD / task 2 to: Tutorial";
        executeCommand(setNameForTask3);

        String setNameForTask4 = "set-name TESTMOD / task 3 to: Event";
        executeCommand(setNameForTask4);

        String setNameForTask5 = "set-name TESTMOD / task 4 to: Exam";
        executeCommand(setNameForTask5);

        String setNameForTask2 = "set-name TESTMOD / task 5 to: Todo";
        executeCommand(setNameForTask2);

        assertEquals(taskList.get(0).toString(), "[LAB][NOT DONE] Lab (at: 01/04/2029 10:00"
                + " to 01/04/2029 12:00)");
        assertEquals(taskList.get(1).toString(), "[TUT][NOT DONE] Tutorial (at: 01/05/2030 10:00"
                + " to 01/05/2030 12:00)");
        assertEquals(taskList.get(2).toString(), "[E][NOT DONE] Event (at: 01/05/2031 10:00"
                + " to 01/05/2031 12:00)");
        assertEquals(taskList.get(3).toString(), "[EXAM][NOT DONE] Exam (at: 01/03/2032 10:00"
                + " to 01/03/2032 12:00)");
        assertEquals(taskList.get(4).toString(), "[T][NOT DONE] Todo");
        testContainer.removeModule(testModule.getModuleCode(),testModule);
    }

    @Test
    public void setNameUnsuccessful_invalidIndexUsed_exceptionThrown() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Todo("Test 1");

        taskList.add(testTask1);

        try {
            String setNameForTask1 = "set-name TESTMOD / task 3 to: Lab";
            executeCommand(setNameForTask1);
            fail();
        } catch (InvalidIndexException e) {
            testContainer.removeModule(testModule.getModuleCode(),testModule);
            assertEquals("Invalid Input\n\nYou have entered an invalid index.", e.getMessage());
        }
    }

    @Test
    public void setDateSuccessful_setDateOfSchedulableTaskToANewDate_taskDateSuccessfullySet() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Deadline("Test 1", new DateTime("01/01/2029 10:00"));
        Task testTask2 = new Exam("Test 2", new DateTime("12/12/2029 10:00"),
                new DateTime("01/03/2030 12:00"));
        Task testTask3 = new Lab("Test 3", new DateTime("01/01/2030 10:00"),
                new DateTime("01/01/2030 12:00"));
        Task testTask4 = new Tutorial("Test 4", new DateTime("01/01/2031 10:00"),
                new DateTime("01/01/2031 12:00"));

        taskList.add(testTask1);
        taskList.add(testTask2);
        taskList.add(testTask3);
        taskList.add(testTask4);

        String setDateForTask1 = "set-date TESTMOD / task 1 to: 02/01/2029 12:00";
        executeCommand(setDateForTask1);

        String setDateForTask2 = "set-date TESTMOD / task 2 to: 02/05/2029 16:00 to 02/05/2029 19:00";
        executeCommand(setDateForTask2);

        String setDateForTask3 = "set-date TESTMOD / task 3 to: 03/02/2030 16:00 to 03/02/2030 19:00";
        executeCommand(setDateForTask3);

        String setDateForTask4 = "set-date TESTMOD / task 4 to: 01/02/2031 16:00 to 01/02/2031 19:00";
        executeCommand(setDateForTask4);

        assertEquals(taskList.get(0).toString(), "[D][NOT DONE] Test 1 (by: 02/01/2029 12:00)");
        assertEquals(taskList.get(1).toString(), "[EXAM][NOT DONE] Test 2 (at: 02/05/2029 16:00"
                + " to 02/05/2029 19:00)");
        assertEquals(taskList.get(2).toString(), "[LAB][NOT DONE] Test 3 (at: 03/02/2030 16:00 "
                + "to 03/02/2030 19:00)");
        assertEquals(taskList.get(3).toString(), "[TUT][NOT DONE] Test 4 (at: 01/02/2031 16:00 "
                + "to 01/02/2031 19:00)");
        testContainer.removeModule(testModule.getModuleCode(),testModule);
    }

    @Test
    public void setDateUnsuccessful_invalidIndexUsed_exceptionThrown() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Todo("Test 1");

        taskList.add(testTask1);

        try {
            String setDateForTask1 = "set-date TESTMOD / task 2 to: 01/03/2029 15:00 to 01/03/2029 19:00";
            executeCommand(setDateForTask1);
            fail();
        } catch (InvalidIndexException e) {
            testContainer.removeModule(testModule.getModuleCode(),testModule);
            assertEquals("Invalid Input\n\nYou have entered an invalid index.", e.getMessage());
        }
    }

    @Test
    public void setDateUnsuccessful_setDateUsedOnNonSchedulableItems_exceptionThrown() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Todo("Test 1");

        taskList.add(testTask1);

        try {
            String setDateForTask1 = "set-date TESTMOD / task 1 to: 01/03/2029 15:00 to 01/03/2029 19:00";
            executeCommand(setDateForTask1);
            fail();
        } catch (InputException e) {
            testContainer.removeModule(testModule.getModuleCode(),testModule);
            assertEquals("Invalid Input\n\nSorry, set-date is not available for a To-Do task.\n"
                    + "Set-date is only available for a Deadline/Event/Exam/Lab/Lecture/Tutorial.", e.getMessage());
        }
    }

    @Test
    public void removeTasksSuccessful_removeOneSingleTaskAndMultipleTasks_tasksSuccessfullyRemoved() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Deadline("Test 1", new DateTime("01/01/2029 10:00"));
        Task testTask2 = new Event("Test 2", new DateTime("01/02/2029 10:00"),
                new DateTime("01/02/2029 12:00"));
        Task testTask3 = new Exam("Test 3", new DateTime("01/03/2029 10:00"),
                new DateTime("01/03/2029 12:00"));
        Task testTask4 = new Lab("Test 4", new DateTime("01/04/2029 10:00"),
                new DateTime("01/04/2029 12:00"));
        Task testTask5 = new Lecture("Test 5", new DateTime("01/05/2029 10:00"),
                new DateTime("01/05/2029 12:00"));
        Task testTask6 = new Tutorial("Test 6", new DateTime("01/06/2029 10:00"),
                new DateTime("01/06/2029 12:00"));

        taskList.add(testTask1);
        taskList.add(testTask2);
        taskList.add(testTask3);
        taskList.add(testTask4);
        taskList.add(testTask5);
        taskList.add(testTask6);

        String removeOneTask = "remove TESTMOD / task 1";
        executeCommand(removeOneTask);

        assertEquals(taskList.size(), 5);

        String removeMultipleTasks = "remove-* TESTMOD / task 1,2,3,4,5";
        executeCommand(removeMultipleTasks);

        assertEquals(taskList.size(), 0);
        testContainer.removeModule(testModule.getModuleCode(),testModule);
    }

    @Test
    public void removeMultipleTasksUnsuccessful_onlyOneIndexProvided_exceptionThrown() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Event("Test 1", new DateTime("01/02/2029 10:00"),
                new DateTime("01/02/2029 12:00"));
        Task testTask2 = new Lecture("Test 2", new DateTime("01/03/2030 10:00"),
                new DateTime("01/03/2030 12:00"));

        taskList.add(testTask1);
        taskList.add(testTask2);

        try {
            String updateTask = "remove-* TESTMOD / task 1";
            executeCommand(updateTask);
            fail();
        } catch (InputException e) {
            testContainer.removeModule(testModule.getModuleCode(),testModule);
            assertEquals("Invalid Input\n\nTo remove a single task, provide the input in this "
                    + "format instead: remove <pageContent> / <type> <one index in integer form>", e.getMessage());
        }
    }

    @Test
    public void updateTasksSuccessful_updateOneSingleTaskAndMultipleTasks_tasksSuccessfullyUpdated() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Event("Test 1", new DateTime("01/02/2029 10:00"),
                new DateTime("01/02/2029 12:00"));
        Task testTask2 = new Exam("Test 2", new DateTime("01/03/2029 10:00"),
                new DateTime("01/03/2029 12:00"));
        Task testTask3 = new Lab("Test 3", new DateTime("01/04/2029 10:00"),
                new DateTime("01/04/2029 12:00"));
        Task testTask4 = new Lecture("Test 4", new DateTime("01/05/2029 10:00"),
                new DateTime("01/05/2029 12:00"));
        Task testTask5 = new Tutorial("Test 5", new DateTime("01/06/2029 10:00"),
                new DateTime("01/06/2029 12:00"));

        taskList.add(testTask1);
        taskList.add(testTask2);
        taskList.add(testTask3);
        taskList.add(testTask4);
        taskList.add(testTask5);

        String updateOneTask = "update TESTMOD / task 1 done";
        executeCommand(updateOneTask);

        assertEquals(taskList.get(4).toString(), "[E][DONE] Test 1 (at: 01/02/2029 10:00 to 01/02/2029 12:00)");

        String updateMultipleTasks = "update-* TESTMOD / task 1,2,3,4 done";
        executeCommand(updateMultipleTasks);

        assertEquals(taskList.get(1).toString(), "[EXAM][DONE] Test 2 (at: 01/03/2029 10:00"
                + " to 01/03/2029 12:00)");
        assertEquals(taskList.get(2).toString(), "[LAB][DONE] Test 3 (at: 01/04/2029 10:00"
                + " to 01/04/2029 12:00)");
        assertEquals(taskList.get(3).toString(), "[LEC][DONE] Test 4 (at: 01/05/2029 10:00"
                + " to 01/05/2029 12:00)");
        assertEquals(taskList.get(4).toString(), "[TUT][DONE] Test 5 (at: 01/06/2029 10:00"
                + " to 01/06/2029 12:00)");

        String updateOneTaskNotDone = "update TESTMOD / task 1 notdone";
        executeCommand(updateOneTaskNotDone);

        assertEquals(taskList.get(0).toString(), "[E][NOT DONE] Test 1 (at: 01/02/2029 10:00 to "
                + "01/02/2029 12:00)");

        String updateMultipleTasksNotDone = "update-* TESTMOD / task 2,3,4,5 notdone";
        executeCommand(updateMultipleTasksNotDone);

        assertEquals(taskList.get(1).toString(), "[EXAM][NOT DONE] Test 2 (at: 01/03/2029 10:00"
                + " to 01/03/2029 12:00)");
        assertEquals(taskList.get(2).toString(), "[LAB][NOT DONE] Test 3 (at: 01/04/2029 10:00"
                + " to 01/04/2029 12:00)");
        assertEquals(taskList.get(3).toString(), "[LEC][NOT DONE] Test 4 (at: 01/05/2029 10:00"
                + " to 01/05/2029 12:00)");
        assertEquals(taskList.get(4).toString(), "[TUT][NOT DONE] Test 5 (at: 01/06/2029 10:00"
                + " to 01/06/2029 12:00)");
        testContainer.removeModule(testModule.getModuleCode(),testModule);
    }

    @Test
    public void updateTasksUnsuccessful_noBooleanValueProvided_exceptionThrown() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Event("Test 1", new DateTime("01/02/2029 10:00"),
                new DateTime("01/02/2029 12:00"));

        taskList.add(testTask1);

        try {
            String updateTask = "update TESTMOD / task 1";
            executeCommand(updateTask);
            fail();
        } catch (InputException e) {
            testContainer.removeModule(testModule.getModuleCode(),testModule);
            assertEquals("Invalid Input\n\nPlease provide an index of item to be updated.", e.getMessage());
        }
    }

    @Test
    public void updateMultipleTasksUnsuccessful_onlyOneIndexProvided_exceptionThrown() throws
            SpinBoxException {
        initializeSetUp();

        taskList = testModule.getTasks();
        clearTaskList();
        Task testTask1 = new Event("Test 1", new DateTime("01/02/2029 10:00"),
                new DateTime("01/02/2029 12:00"));
        Task testTask2 = new Lecture("Test 2", new DateTime("01/03/2030 10:00"),
                new DateTime("01/03/2030 12:00"));

        taskList.add(testTask1);
        taskList.add(testTask2);

        try {
            String updateTask = "update-* TESTMOD / task 1 done";
            executeCommand(updateTask);
            fail();
        } catch (InputException e) {
            testContainer.removeModule(testModule.getModuleCode(),testModule);
            assertEquals("Invalid Input\n\nTo update a single task, provide the input in this "
                    + "format instead: update <pageContent> / <type> <one index in integer form> "
                    + "<done status>", e.getMessage());
        }
    }
}