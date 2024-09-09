package ru.yandex.practicum.managers;

import ru.yandex.practicum.interfaces.HistoryManager;
import ru.yandex.practicum.models.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task task;
        Node prev;
        Node next;

        public Node(Task task) {
            this.task = task;
        }
    }

    private Node head;
    private Node tail;
    private final HashMap<Integer, Node> taskMap = new HashMap<>();


    private void linkLast(Task task) {

        if (taskMap.containsKey(task.getTaskId())) {
            removeNode(taskMap.get(task.getTaskId()));
        }

        Node newNode = new Node(task);
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }


        taskMap.put(task.getTaskId(), newNode);
    }


    public List<Task> getTasks() {
        List<Task> tasks = new ArrayList<>();
        Node current = head;
        while (current != null) {
            tasks.add(current.task);
            current = current.next;
        }
        return tasks;
    }


    private void removeNode(Node node) {
        if (node == null) return;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }


        taskMap.remove(node.task.getTaskId());
    }

    private final ArrayList<Task> history = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            history.remove(task);
            history.add(task);
        }
    }

    @Override
    public void remove(int id) {
        for (Task task : new ArrayList<>(history)) {
            if (task.getTaskId() == id) {
                history.remove(task);
                break;
            }
        }
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}


