package com.example.taskapi.auth;

import java.util.Arrays;

/**
 * Хеш-таблица с закрытой адресацией (open addressing)
 * и линейным пробированием (linear probing).
 * Ключ — String (логин), значение — String (хеш пароля).
 */
public class HashTable {

    private static class Entry {
        String key;
        String value;
        boolean deleted;

        Entry(String key, String value) {
            this.key = key;
            this.value = value;
            this.deleted = false;
        }
    }

    private Entry[] table;
    private int size;
    private int capacity;
    private static final double LOAD_FACTOR = 0.7;

    public HashTable() {
        this.capacity = 16;
        this.table = new Entry[capacity];
        this.size = 0;
    }

    /** Хеш-функция: суммируем коды символов и берём по модулю capacity. */
    private int hash(String key) {
        int h = 0;
        for (int i = 0; i < key.length(); i++) {
            h = (h * 31 + key.charAt(i)) & 0x7fffffff;
        }
        return h % capacity;
    }

    /** Вставка или обновление значения. */
    public void put(String key, String value) {
        if ((double) size / capacity >= LOAD_FACTOR) {
            resize();
        }
        int index = hash(key);
        int start = index;
        while (table[index] != null && !table[index].deleted
               && !table[index].key.equals(key)) {
            index = (index + 1) % capacity;
            if (index == start) {
                resize();
                put(key, value);
                return;
            }
        }
        if (table[index] == null || table[index].deleted) {
            size++;
        }
        table[index] = new Entry(key, value);
    }

    /** Получение значения по ключу. */
    public String get(String key) {
        int index = hash(key);
        int start = index;
        while (table[index] != null) {
            if (!table[index].deleted && table[index].key.equals(key)) {
                return table[index].value;
            }
            index = (index + 1) % capacity;
            if (index == start) break;
        }
        return null;
    }

    /** Удаление по ключу (ленивое — помечаем deleted). */
    public boolean remove(String key) {
        int index = hash(key);
        int start = index;
        while (table[index] != null) {
            if (!table[index].deleted && table[index].key.equals(key)) {
                table[index].deleted = true;
                size--;
                return true;
            }
            index = (index + 1) % capacity;
            if (index == start) break;
        }
        return false;
    }

    public boolean containsKey(String key) {
        return get(key) != null;
    }

    public int size() {
        return size;
    }

    /** Расширение таблицы при превышении load factor. */
    private void resize() {
        Entry[] old = table;
        capacity *= 2;
        table = new Entry[capacity];
        size = 0;
        for (Entry e : old) {
            if (e != null && !e.deleted) {
                put(e.key, e.value);
            }
        }
    }
}