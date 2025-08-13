package edu.jose.vazquez.actividades.actividad1;

import java.util.NoSuchElementException;
import java.util.Scanner;

/* =====================================================
   MusicPlaylistCLI: simulador de playlists por consola
   - Modos: NORMAL (Singly), NAVEGABLE (Doubly), BUCLE (Circular)
   - Operaciones: agregar, eliminar, listar, play, next, prev, goto
   ===================================================== */
public class MusicPlaylistManager {

    /* =========
       Clase Canción
       ========= */
    static class Song {
        String title;
        String artist;
        double duration; // min

        Song(String title, String artist, double duration) {
            this.title = title;
            this.artist = artist;
            this.duration = duration;
        }
        @Override
        public String toString() {
            return "\"" + title + "\" - " + artist + " (" + String.format("%.2f", duration) + " min)";
        }
        // igualdad por título+artista (case-insensitive)
        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Song)) return false;
            Song s = (Song) o;
            return title.equalsIgnoreCase(s.title) && artist.equalsIgnoreCase(s.artist);
        }
        @Override public int hashCode() { return (title.toLowerCase()+"|"+artist.toLowerCase()).hashCode(); }
    }

    /* =======================
       Interfaz de listas
       ======================= */
    interface MyList<T> {
        void addFirst(T value);
        void addLast(T value);
        void addAt(int index, T value);
        T removeFirst();
        T removeLast();
        T removeAt(int index);
        boolean remove(T value);
        T get(int index);
        boolean contains(T value);
        int size();
        boolean isEmpty();
        void clear();
    }

    /* ===========================
       Lista Simplemente Enlazada
       =========================== */
    static class SinglyLinkedList<T> implements MyList<T> {
        private static class Node<T> { T data; Node<T> next; Node(T d){data=d;} }
        private Node<T> head, tail;
        private int size;

        @Override public void addFirst(T v){ Node<T> n=new Node<>(v); n.next=head; head=n; if(tail==null) tail=n; size++; }
        @Override public void addLast(T v){ Node<T> n=new Node<>(v); if(tail==null){head=tail=n;} else {tail.next=n; tail=n;} size++; }
        @Override public void addAt(int i,T v){ checkPos(i); if(i==0){addFirst(v);return;} if(i==size){addLast(v);return;} Node<T> p=nodeAt(i-1); Node<T> n=new Node<>(v); n.next=p.next; p.next=n; size++; }
        @Override public T removeFirst(){ ensureNotEmpty(); T val=head.data; head=head.next; if(head==null) tail=null; size--; return val; }
        @Override public T removeLast(){ ensureNotEmpty(); if(head==tail){T val=head.data; head=tail=null; size=0; return val;} Node<T> p=head; while(p.next!=tail) p=p.next; T val=tail.data; tail=p; tail.next=null; size--; return val; }
        @Override public T removeAt(int i){ checkElem(i); if(i==0) return removeFirst(); Node<T> p=nodeAt(i-1); T val=p.next.data; if(p.next==tail) tail=p; p.next=p.next.next; size--; return val; }
        @Override public boolean remove(T v){ if(isEmpty()) return false; if(eq(head.data,v)){ removeFirst(); return true;} Node<T> p=head; while(p.next!=null && !eq(p.next.data,v)) p=p.next; if(p.next==null) return false; if(p.next==tail) tail=p; p.next=p.next.next; size--; return true; }
        @Override public T get(int i){ checkElem(i); return nodeAt(i).data; }
        @Override public boolean contains(T v){ return indexOf(v)!=-1; }
        @Override public int size(){ return size; }
        @Override public boolean isEmpty(){ return size==0; }
        @Override public void clear(){ head=tail=null; size=0; }

        public int indexOf(T v){ int i=0; for(Node<T> n=head;n!=null;n=n.next,i++) if(eq(n.data,v)) return i; return -1; }
        private Node<T> nodeAt(int i){ Node<T> n=head; for(int k=0;k<i;k++) n=n.next; return n; }
        private void ensureNotEmpty(){ if(isEmpty()) throw new NoSuchElementException("Lista vacía"); }
        private void checkElem(int i){ if(i<0||i>=size) throw new IndexOutOfBoundsException("Index: "+i+", size: "+size); }
        private void checkPos(int i){ if(i<0||i>size) throw new IndexOutOfBoundsException("Index: "+i+", size: "+size); }
        private boolean eq(T a,T b){ return (a==b) || (a!=null && a.equals(b)); }
        @Override public String toString(){ StringBuilder sb=new StringBuilder("["); for(Node<T> n=head;n!=null;n=n.next){ sb.append(n.data); if(n.next!=null) sb.append(" -> "); } return sb.append("]").toString(); }
    }

    /* ==========================
       Lista Doblemente Enlazada
       ========================== */
    static class DoublyLinkedList<T> implements MyList<T> {
        private static class Node<T>{ T data; Node<T> prev,next; Node(T d){data=d;} }
        private Node<T> head, tail; int size;

        @Override public void addFirst(T v){ Node<T> n=new Node<>(v); n.next=head; if(head!=null) head.prev=n; head=n; if(tail==null) tail=n; size++; }
        @Override public void addLast(T v){ Node<T> n=new Node<>(v); n.prev=tail; if(tail!=null) tail.next=n; tail=n; if(head==null) head=n; size++; }
        @Override public void addAt(int i,T v){ checkPos(i); if(i==0){addFirst(v);return;} if(i==size){addLast(v);return;} Node<T> c=nodeAt(i); Node<T> n=new Node<>(v); n.prev=c.prev; n.next=c; c.prev.next=n; c.prev=n; size++; }
        @Override public T removeFirst(){ ensureNotEmpty(); T val=head.data; head=head.next; if(head!=null) head.prev=null; else tail=null; size--; return val; }
        @Override public T removeLast(){ ensureNotEmpty(); T val=tail.data; tail=tail.prev; if(tail!=null) tail.next=null; else head=null; size--; return val; }
        @Override public T removeAt(int i){ checkElem(i); if(i==0) return removeFirst(); if(i==size-1) return removeLast(); Node<T> c=nodeAt(i); c.prev.next=c.next; c.next.prev=c.prev; size--; return c.data; }
        @Override public boolean remove(T v){ for(Node<T> n=head;n!=null;n=n.next){ if(eq(n.data,v)){ if(n==head){removeFirst();} else if(n==tail){removeLast();} else { n.prev.next=n.next; n.next.prev=n.prev; size--; } return true; } } return false; }
        @Override public T get(int i){ checkElem(i); return nodeAt(i).data; }
        @Override public boolean contains(T v){ return indexOf(v)!=-1; }
        @Override public int size(){ return size; }
        @Override public boolean isEmpty(){ return size==0; }
        @Override public void clear(){ head=tail=null; size=0; }

        public int indexOf(T v){ int i=0; for(Node<T> n=head;n!=null;n=n.next,i++) if(eq(n.data,v)) return i; return -1; }
        private Node<T> nodeAt(int i){ if(i<size/2){ Node<T> n=head; for(int k=0;k<i;k++) n=n.next; return n; } else { Node<T> n=tail; for(int k=size-1;k>i;k--) n=n.prev; return n; } }
        private void ensureNotEmpty(){ if(isEmpty()) throw new NoSuchElementException("Lista vacía"); }
        private void checkElem(int i){ if(i<0||i>=size) throw new IndexOutOfBoundsException("Index: "+i+", size: "+size); }
        private void checkPos(int i){ if(i<0||i>size) throw new IndexOutOfBoundsException("Index: "+i+", size: "+size); }
        private boolean eq(T a,T b){ return (a==b) || (a!=null && a.equals(b)); }
        @Override public String toString(){ StringBuilder sb=new StringBuilder("["); for(Node<T> n=head;n!=null;n=n.next){ sb.append(n.data); if(n.next!=null) sb.append(" <-> "); } return sb.append("]").toString(); }
    }

    /* =======================================
       Lista Circular Simplemente Enlazada
       ======================================= */
    static class CircularSinglyLinkedList<T> implements MyList<T> {
        private static class Node<T>{ T data; Node<T> next; Node(T d){data=d;} }
        private Node<T> tail; int size;

        @Override public void addFirst(T v){ Node<T> n=new Node<>(v); if(tail==null){tail=n; tail.next=tail;} else { n.next=tail.next; tail.next=n; } size++; }
        @Override public void addLast(T v){ addFirst(v); tail=tail.next; }
        @Override public void addAt(int i,T v){ checkPos(i); if(i==0){addFirst(v);return;} if(i==size){addLast(v);return;} Node<T> p=nodeAt(i-1); Node<T> n=new Node<>(v); n.next=p.next; p.next=n; size++; }
        @Override public T removeFirst(){ ensureNotEmpty(); Node<T> h=tail.next; T val=h.data; if(tail==h) tail=null; else tail.next=h.next; size--; return val; }
        @Override public T removeLast(){ ensureNotEmpty(); if(size==1) return removeFirst(); Node<T> p=tail.next; while(p.next!=tail) p=p.next; T val=tail.data; p.next=tail.next; tail=p; size--; return val; }
        @Override public T removeAt(int i){ checkElem(i); if(i==0) return removeFirst(); if(i==size-1) return removeLast(); Node<T> p=nodeAt(i-1); T val=p.next.data; p.next=p.next.next; size--; return val; }
        @Override public boolean remove(T v){ if(isEmpty()) return false; Node<T> prev=tail, cur=tail.next; for(int i=0;i<size;i++){ if(eq(cur.data,v)){ if(cur==tail) tail=prev; if(size==1) tail=null; else prev.next=cur.next; size--; return true; } prev=cur; cur=cur.next; } return false; }
        @Override public T get(int i){ checkElem(i); return nodeAt(i).data; }
        @Override public boolean contains(T v){ return indexOf(v)!=-1; }
        @Override public int size(){ return size; }
        @Override public boolean isEmpty(){ return size==0; }
        @Override public void clear(){ tail=null; size=0; }

        public int indexOf(T v){ if(isEmpty()) return -1; Node<T> n=tail.next; for(int i=0;i<size;i++,n=n.next) if(eq(n.data,v)) return i; return -1; }
        private Node<T> nodeAt(int i){ Node<T> n=tail.next; for(int k=0;k<i;k++) n=n.next; return n; }
        private void ensureNotEmpty(){ if(isEmpty()) throw new NoSuchElementException("Lista vacía"); }
        private void checkElem(int i){ if(i<0||i>=size) throw new IndexOutOfBoundsException("Index: "+i+", size: "+size); }
        private void checkPos(int i){ if(i<0||i>size) throw new IndexOutOfBoundsException("Index: "+i+", size: "+size); }
        private boolean eq(T a,T b){ return (a==b) || (a!=null && a.equals(b)); }
        @Override public String toString(){ if(isEmpty()) return "[]"; StringBuilder sb=new StringBuilder("["); Node<T> n=tail.next; for(int i=0;i<size;i++){ sb.append(n.data); if(i<size-1) sb.append(" -> "); n=n.next; } return sb.append("] (circular)").toString(); }
    }

    /* ==============
       Player & Modo
       ============== */
    enum Mode { NORMAL, NAVEGABLE, BUCLE }

    static class Player {
        Mode mode = Mode.NORMAL;
        final MyList<Song> normal = new SinglyLinkedList<>();
        final DoublyLinkedList<Song> navegable = new DoublyLinkedList<>();
        final MyList<Song> bucle = new CircularSinglyLinkedList<>();

        // índices/cursor actuales por modo
        int idxNavegable = -1; // -1 = nada seleccionado
        int idxBucle = -1;

        void setMode(Mode m){
            mode = m;
            System.out.println("Modo cambiado a: " + mode);
        }

        void addSong(Song s){
            normal.addLast(s);
            navegable.addLast(s);
            bucle.addLast(s);
            if(idxNavegable==-1 && navegable.size()>0) idxNavegable = 0;
            if(idxBucle==-1 && bucle.size()>0) idxBucle = 0;
            System.out.println("Agregada: " + s);
        }

        boolean removeSong(String title){
            Song dummy = findFirstByTitle(title);
            if(dummy==null){ System.out.println("No encontrada: " + title); return false; }
            // ajustar cursores si afectan
            int posNav = navegable.indexOf(dummy);
            int posBuc = ((CircularSinglyLinkedList<Song>)bucle).indexOf(dummy);
            boolean r1 = normal.remove(dummy);
            boolean r2 = navegable.remove(dummy);
            boolean r3 = bucle.remove(dummy);

            if(r2 && idxNavegable!=-1){
                if(navegable.size()==0) idxNavegable=-1;
                else if(posNav < idxNavegable) idxNavegable--;
                else if(posNav == idxNavegable && idxNavegable == navegable.size()) idxNavegable--; // si borramos el último
            }
            if(r3 && idxBucle!=-1){
                if(bucle.size()==0) idxBucle=-1;
                else if(posBuc < idxBucle) idxBucle--;
                else if(posBuc == idxBucle && idxBucle == bucle.size()) idxBucle--;
            }
            System.out.println("Eliminada: " + dummy);
            return r1||r2||r3;
        }

        Song findFirstByTitle(String title){
            for(int i=0;i<normal.size();i++){
                Song s = normal.get(i);
                if(s.title.equalsIgnoreCase(title)) return s;
            }
            return null;
        }

        void list(){
            System.out.println("--- NORMAL ---");
            System.out.println(normal);
            System.out.println("--- NAVEGABLE ---");
            System.out.println(navegable);
            System.out.println("--- BUCLE ---");
            System.out.println(bucle);
        }

        void play(){
            Song s = current();
            if(s==null) System.out.println("No hay canción seleccionada.");
            else System.out.println("▶ Reproduciendo: " + s);
        }

        Song current(){
            switch (mode){
                case NORMAL:
                    if(normal.isEmpty()) return null;
                    // Por sencillez, en NORMAL el "actual" es el primero
                    return normal.get(0);
                case NAVEGABLE:
                    if(idxNavegable==-1) return null;
                    return navegable.get(idxNavegable);
                case BUCLE:
                    if(idxBucle==-1) return null;
                    return bucle.get(idxBucle);
            }
            return null;
        }

        void next(){
            switch (mode){
                case NORMAL:
                    if(normal.isEmpty()){System.out.println("Playlist vacía."); return;}
                    // mover primero al final
                    Song s = normal.removeFirst();
                    normal.addLast(s);
                    System.out.println("Siguiente ► " + normal.get(0));
                    break;
                case NAVEGABLE:
                    if(navegable.isEmpty()){System.out.println("Playlist vacía."); return;}
                    if(idxNavegable < navegable.size()-1) idxNavegable++;
                    System.out.println("Siguiente ► " + navegable.get(idxNavegable));
                    break;
                case BUCLE:
                    if(bucle.isEmpty()){System.out.println("Playlist vacía."); return;}
                    idxBucle = (idxBucle + 1) % bucle.size();
                    System.out.println("Siguiente ► " + bucle.get(idxBucle));
                    break;
            }
        }

        void prev(){
            switch (mode){
                case NORMAL:
                    System.out.println("En modo NORMAL no hay retroceso dedicado.");
                    break;
                case NAVEGABLE:
                    if(navegable.isEmpty()){System.out.println("Playlist vacía."); return;}
                    if(idxNavegable > 0) idxNavegable--;
                    System.out.println("Anterior ◄ " + navegable.get(idxNavegable));
                    break;
                case BUCLE:
                    if(bucle.isEmpty()){System.out.println("Playlist vacía."); return;}
                    idxBucle = (idxBucle - 1 + bucle.size()) % bucle.size();
                    System.out.println("Anterior ◄ " + bucle.get(idxBucle));
                    break;
            }
        }

        void gotoIndex(int i){
            switch (mode){
                case NORMAL:
                    if(i<0 || i>=normal.size()){ System.out.println("Índice fuera de rango."); return; }
                    // rotar la lista hasta dejar ese índice en 0
                    int rot = i;
                    while(rot-- > 0){ Song s = normal.removeFirst(); normal.addLast(s); }
                    System.out.println("Posicionado en: " + normal.get(0));
                    break;
                case NAVEGABLE:
                    if(i<0 || i>=navegable.size()){ System.out.println("Índice fuera de rango."); return; }
                    idxNavegable = i;
                    System.out.println("Posicionado en: " + navegable.get(idxNavegable));
                    break;
                case BUCLE:
                    if(i<0 || i>=bucle.size()){ System.out.println("Índice fuera de rango."); return; }
                    idxBucle = i;
                    System.out.println("Posicionado en: " + bucle.get(idxBucle));
                    break;
            }
        }

        void clearAll(){
            normal.clear(); navegable.clear(); bucle.clear();
            idxNavegable = -1; idxBucle = -1;
            System.out.println("Playlists vaciadas.");
        }
    }

    /* ========
       MAIN CLI
       ======== */
    public static void main(String[] args) {
        Player p = new Player();
        // demo inicial
        p.addSong(new Song("Bohemian Rhapsody", "Queen", 5.55));
        p.addSong(new Song("Shape of You", "Ed Sheeran", 3.45));
        p.addSong(new Song("Blinding Lights", "The Weeknd", 3.20));
        p.addSong(new Song("Imagine", "John Lennon", 3.15));
        p.setMode(Mode.NAVEGABLE); // arranca navegable

        Scanner sc = new Scanner(System.in);
        while (true){
            printMenu(p.mode);
            String op = sc.nextLine().trim().toLowerCase();

            try {
                switch (op){
                    case "1": { // cambiar modo
                        System.out.println("Elige modo: 1) NORMAL, 2) NAVEGABLE, 3) BUCLE");
                        String m = sc.nextLine().trim();
                        if ("1".equals(m)) p.setMode(Mode.NORMAL);
                        else if ("2".equals(m)) p.setMode(Mode.NAVEGABLE);
                        else if ("3".equals(m)) p.setMode(Mode.BUCLE);
                        else System.out.println("Opción inválida.");
                        break;
                    }
                    case "2": { // agregar
                        System.out.print("Título: "); String t = sc.nextLine();
                        System.out.print("Artista: "); String a = sc.nextLine();
                        System.out.print("Duración (min): "); double d = readDouble(sc);
                        p.addSong(new Song(t,a,d));
                        break;
                    }
                    case "3": { // eliminar por título
                        System.out.print("Título a eliminar: "); String t = sc.nextLine();
                        p.removeSong(t);
                        break;
                    }
                    case "4": p.list(); break;
                    case "5": p.play(); break;
                    case "6": p.next(); break;
                    case "7": p.prev(); break;
                    case "8": { // goto
                        System.out.print("Índice destino (0..n-1): "); int i = readInt(sc);
                        p.gotoIndex(i);
                        break;
                    }
                    case "9": p.clearAll(); break;
                    case "0": System.out.println("Saliendo..."); return;
                    default: System.out.println("Opción inválida.");
                }
            } catch (Exception e){
                System.out.println("Error: " + e.getMessage());
            }
            System.out.println();
        }
    }

    /* Utilidades lectura segura */
    private static double readDouble(Scanner sc){
        while (true){
            String s = sc.nextLine().trim();
            try { return Double.parseDouble(s); }
            catch (NumberFormatException e){ System.out.print("Número inválido. Intenta de nuevo: "); }
        }
    }
    private static int readInt(Scanner sc){
        while (true){
            String s = sc.nextLine().trim();
            try { return Integer.parseInt(s); }
            catch (NumberFormatException e){ System.out.print("Entero inválido. Intenta de nuevo: "); }
        }
    }

    private static void printMenu(Mode mode){
        System.out.println("===== MUSIC PLAYLIST MANAGER =====");
        System.out.println("Modo actual: " + mode);
        System.out.println("1) Cambiar modo (Normal/Navegable/Bucle)");
        System.out.println("2) Agregar canción");
        System.out.println("3) Eliminar canción por título");
        System.out.println("4) Listar playlists");
        System.out.println("5) Play (ver canción actual)");
        System.out.println("6) Next (siguiente)");
        System.out.println("7) Prev (anterior)");
        System.out.println("8) Ir a índice (goto)");
        System.out.println("9) Limpiar playlists");
        System.out.println("0) Salir");
        System.out.print("Elige opción: ");
    }
}


