import java.io.BufferedReader;
import javax.swing.JFileChooser;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import static java.lang.Character.isLetter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Geoff
 */

public class Wordbuzzer extends javax.swing.JFrame {
    
    public class Entry {
        int id;
        String name;
        String skills;
        
        public Entry(){
            id = -1;
            name = new String("Untitled Job");
            skills = new String("");
        }
        public Entry(int i, String n, String s){
            this.id = i;
            this.name = n;
            this.skills = s;
        }
    }
    public class Phrase {
        String words;
        int count;
        
        public Phrase(){
            words = new String();
            count = 0;
        }
        
        public Phrase(String str){
            words = str;
            count = 0;
        }
        
        public Phrase(String str, int c){
            words = str;
            count = c;
        }
        
        public Boolean equals(String str){
            if (this.words.equals(str))
                return true;
            else
                return false;
        }
        
        public void inc(){
            this.count++;
        }
        public String toString(){
            return this.words + ": " + Integer.toString(this.count);
        }
    }
    
    ArrayList<String> stoplist= new ArrayList<String>(Arrays.asList(
                                      "a",  "an", "and", "are", "as", "at",
                                      "be", "by", "for", "has", "he", "in",
                                      "is", "it", "its", "of",  "on", "that",
                                      "the", "to",  "was", "were", "with"));
    String excludeThis;
    
    ArrayList<Entry> entryList;
    Entry currentEntry = new Entry();
    
    HashMap<String, Integer> h1 = new HashMap<String, Integer>();
    HashMap<String, Integer> h2 = new HashMap<String, Integer>();
    HashMap<String, Integer> h3 = new HashMap<String, Integer>();
    ArrayList<Phrase> p1 = new ArrayList<Phrase>();
    ArrayList<Phrase> p2 = new ArrayList<Phrase>();
    ArrayList<Phrase> p3 = new ArrayList<Phrase>();
    
    String popMode = "unset";
    
    public Wordbuzzer() {
        initComponents();
        excludeThis = null;
        Entry currentEntry = new Entry();
        entryList = new ArrayList<Entry>();
        updateStoplist();
        
    }

    ///Tom methods////////////////////////////////
    /*public void findDirectory(){
        int returnVal = STOPSAVE_CHOOSER.showOpenDialog(this);
        if(returnVal == JFileChooser.APPROVE_OPTION){
            File file = STOPSAVE_CHOOSER.getSelectedFile();
            FOLDER_PATH_TEXT.setText(file.getPath());
            directory = file.getAbsolutePath();
        }
    }*/
    public void updateStoplist(){
        STOPLIST.removeAll();
        for(String s : stoplist)
            STOPLIST.add(s, WIDTH);
    }
    
    public void addStopWord(){
        String word = NEW_STOPWORD.getText();
        stoplist.add(word);
        STOPLIST.add(word, WIDTH);
    }
    
    public void removeStopWord(){
       String word = STOPLIST.getSelectedItem(); 
        stoplist.remove(word);
        STOPLIST.remove(word);}
    
    public void popOpen(String pop){
        switch(pop){
            case "saveStoplist":
            {
                POPLABEL.setText("Copy and save somewhere for another time:");
                return;
            }
            case "loadStoplist":
            {
                POPLABEL.setText("Paste list of words to exclude:");
                loadStoplist();
                return;
            }
            case "saveJoblist":
            {
                POPLABEL.setText("Copy and save somewhere for another time:");
                POPTEXT.setText(saveJoblist());
                return;
            }  
            case "loadJoblist":
            {
                POPLABEL.setText("Paste output from Wordbuzzer Job List Save");
                loadStoplist();
                return;
            }
            case "saveResults":
            {
                POPLABEL.setText("Copy and save somewhere:");
                saveResults();
                return;
            }
            default:{
                System.out.print("Error: invalid pop open switch");
                return;
            }
        }
    }
    
    public void popFx(String pop){
        switch(pop){
            case "saveStoplist":
            {
                saveStoplist();
                return;
            }
            case "loadStoplist":
            {
                loadStoplist();
                return;
            }
            case "saveJoblist":
            {
                saveJoblist();
                return;
            }  
            case "saveResults":
            {
                saveResults();
                return;
            }
            default:{
                System.out.print("Error: invalid pop fx switch");
                return;
            }
        }
    }
    
    public String saveStoplist(){
        String list = "";
        for(String s : stoplist)
            list = list + s + "\n";
        return list;
    }
    
    public void loadStoplist(){
        String[] list = POPTEXT.getText().split("\n");
        stoplist = new ArrayList<String>(Arrays.asList(list));
        updateStoplist();
    }                        
    
    public String saveJoblist(){
        String list = "";
        for(Entry e : entryList)
            list = list + e.name + "||" + e.skills + "\n^^\n";
        return list;
    }
    
    public void loadJoblist(){
        ENTRY_LIST.removeAll();
        entryList.clear();
        String[] list = POPTEXT.getText().split("\n^^\n");
        ArrayList<String> tempList = new ArrayList<String>(Arrays.asList(list));
        for(String s : tempList){
            String[] both = s.split("||");
            Entry e = new Entry(entryList.size(), both[0], both[1]);
            entryList.add(e);
            ENTRY_LIST.add(e.name);
        }
    }
    
    public void saveResults(){
        
    }
    
    
        
   ////////////////////// public void loadStoplist(){}
    
    
    
    public void analyze(){
        SINGLES.removeAll();
        PAIRS.removeAll();
        TRIPLETS.removeAll();
        p1.clear();
        p2.clear();
        p3.clear();
        h1.clear();
        h2.clear();
        h3.clear();
        
        for(Entry entry: entryList){
            
            ArrayList<String> s1 = new ArrayList<String>();
            ArrayList<String> s2 = new ArrayList<String>();
            ArrayList<String> s3 = new ArrayList<String>();
            
            ArrayList<String> newLines = new ArrayList<String>(
                    Arrays.asList(entry.skills.toLowerCase().split("\n")));
            
            for(String l : newLines){
                ArrayList<String> words = new ArrayList<String>(
                    Arrays.asList(l.split(" ")));
                String word = "";
                String lastword = "";
                String nextlastword = "";
                for(String w : words){
                    word = w.replaceAll("[^a-zA-Z0-9-]", "");
                    if(!s1.contains(word))
                        s1.add(word);
                    if(!lastword.equals("")){
                        if(!s2.contains(word))
                            s2.add(lastword + " " + word); 
                    }
                    if(!nextlastword.equals("")){
                        if(!s3.contains(word))
                            s3.add(nextlastword + " " + lastword + " " + word);
                    }
                    nextlastword = lastword;
                    lastword = word;
                }
            } 
            addEntry(s1, s2, s3);
        }
        displayResults();
    }
    
    public void addEntry(ArrayList<String> s1, ArrayList<String> s2, ArrayList<String> s3){
        for(String s : s1){
            if(stoplist.contains(s)){
                continue;
            }else if(h1.containsKey(s)){
                int i = h1.get(s);
                p1.get(i).inc();
            }else{
                Phrase temp = new Phrase(s, 1);
                int i = p1.size();
                p1.add(i, temp);
                h1.put(s, i);
            }
                
        }
        
        for(String s : s2){
            String[] two = s.split(" ");
            if(stoplist.contains(two[0]) || stoplist.contains(two[1])){
                continue;
            }else if(h2.containsKey(s)){
                int i = h2.get(s);
                p2.get(i).inc();
            }else{
                Phrase temp = new Phrase(s, 1);
                int i = p2.size();
                p2.add(i, temp);
                h2.put(s, i);
            }
        }
        
        for(String s : s3){
            String[] three = s.split(" ");
            int stopcount = 0;
            for(int c = 0; c < 3; c++){
                if(stoplist.contains(three[c]))
                    stopcount++;
            }
            if(stopcount > 1){
                continue;
            }else if(h3.containsKey(s)){
                int i = h3.get(s);
                p3.get(i).inc();
            }else{
                Phrase temp = new Phrase(s, 1);
                int i = p3.size();
                p3.add(i, temp);
                h3.put(s, i);
            }
        }
    }
    
    
    public void displayResults(){
        Collections.sort(p1, new Comparator<Phrase>() {
                @Override
                public int compare(Phrase lhs, Phrase rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.count > rhs.count ? -1 : (lhs.count < rhs.count ) ? 1 : 0;
                }
            });
        Collections.sort(p2, new Comparator<Phrase>() {
                @Override
                public int compare(Phrase lhs, Phrase rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.count > rhs.count ? -1 : (lhs.count < rhs.count ) ? 1 : 0;
                }
            });
        Collections.sort(p3, new Comparator<Phrase>() {
                @Override
                public int compare(Phrase lhs, Phrase rhs) {
                    // -1 - less than, 1 - greater than, 0 - equal, all inversed for descending
                    return lhs.count > rhs.count ? -1 : (lhs.count < rhs.count ) ? 1 : 0;
                }
            });
        
        
        for(Phrase p : p1){
            if (p.count > 1)
                SINGLES.add(p.toString());
        }
        for(Phrase p : p2){
            if (p.count > 1)
                PAIRS.add(p.toString());
        }
        for(Phrase p : p3){
            if (p.count > 1)
                TRIPLETS.add(p.toString());
        }
    }
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        POPUP = new javax.swing.JDialog();
        POPTEXT = new java.awt.TextArea();
        POPOK = new javax.swing.JButton();
        POPLABEL = new javax.swing.JLabel();
        POPWARNING = new javax.swing.JLabel();
        MAIN_PANEL_1 = new javax.swing.JPanel();
        STOPLIST = new java.awt.List();
        NEW_STOPWORD = new java.awt.TextField();
        ADD_STOPWORD = new java.awt.Button();
        REMOVE_STOPWORD = new java.awt.Button();
        SAVE_STOPLIST = new javax.swing.JButton();
        WORDSNOTCOUNTED = new javax.swing.JLabel();
        TITLE_AUTHOR_PANEL = new javax.swing.JPanel();
        TITLE = new javax.swing.JLabel();
        AUTHOR = new javax.swing.JLabel();
        LOAD_STOPLIST = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        MAIN_PANEL_2 = new javax.swing.JPanel();
        DELETE_ENTRY = new java.awt.Button();
        NEW_ENTRY = new java.awt.Button();
        SKILLS_ENTRY = new java.awt.TextArea();
        SAVE_ENTRY = new java.awt.Button();
        ENTRY_NAME = new java.awt.TextField();
        ENTRY_LIST = new java.awt.List();
        POSTINGS_LABEL = new java.awt.Label();
        MAIN_PANEL_3 = new javax.swing.JPanel();
        OCCURRENCE_HEADER = new javax.swing.JLabel();
        ANALYZE = new javax.swing.JButton();
        SINGLES = new java.awt.List();
        TRIPLETS = new java.awt.List();
        PAIRS = new java.awt.List();
        SAVE_RESULTS = new javax.swing.JButton();
        SAVE_JOBLIST = new javax.swing.JButton();

        POPOK.setText("OK");
        POPOK.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                POPOKActionPerformed(evt);
            }
        });

        POPLABEL.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        POPLABEL.setText("Instructions");

        POPWARNING.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N
        POPWARNING.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        POPWARNING.setText("(Save/paste exactly as formatted by Wordbuzzer)");

        javax.swing.GroupLayout POPUPLayout = new javax.swing.GroupLayout(POPUP.getContentPane());
        POPUP.getContentPane().setLayout(POPUPLayout);
        POPUPLayout.setHorizontalGroup(
            POPUPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(POPLABEL, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(POPUPLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(POPUPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(POPWARNING, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(POPUPLayout.createSequentialGroup()
                        .addComponent(POPTEXT, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(POPUPLayout.createSequentialGroup()
                .addGap(73, 73, 73)
                .addComponent(POPOK, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        POPUPLayout.setVerticalGroup(
            POPUPLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(POPUPLayout.createSequentialGroup()
                .addComponent(POPLABEL, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(POPWARNING)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(POPTEXT, javax.swing.GroupLayout.DEFAULT_SIZE, 252, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(POPOK)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        STOPLIST.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        STOPLIST.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        NEW_STOPWORD.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        NEW_STOPWORD.setName("BADWORD"); // NOI18N

        ADD_STOPWORD.setLabel("^^  Add  ^^");
        ADD_STOPWORD.setName("EXCLUDE"); // NOI18N
        ADD_STOPWORD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ADD_STOPWORDActionPerformed(evt);
            }
        });

        REMOVE_STOPWORD.setLabel("<<   Remove");
        REMOVE_STOPWORD.setName("REMOVEEXCLUSION"); // NOI18N
        REMOVE_STOPWORD.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                REMOVE_STOPWORDActionPerformed(evt);
            }
        });

        SAVE_STOPLIST.setText("Save list...");
        SAVE_STOPLIST.setName("SAVEEXCLUDE"); // NOI18N
        SAVE_STOPLIST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVE_STOPLISTActionPerformed(evt);
            }
        });

        WORDSNOTCOUNTED.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        WORDSNOTCOUNTED.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        WORDSNOTCOUNTED.setText("Ignore List:");

        TITLE.setFont(new java.awt.Font("Tahoma", 1, 36)); // NOI18N
        TITLE.setText("WORDBUZZER");

        AUTHOR.setText("by Tom ^_^");

        javax.swing.GroupLayout TITLE_AUTHOR_PANELLayout = new javax.swing.GroupLayout(TITLE_AUTHOR_PANEL);
        TITLE_AUTHOR_PANEL.setLayout(TITLE_AUTHOR_PANELLayout);
        TITLE_AUTHOR_PANELLayout.setHorizontalGroup(
            TITLE_AUTHOR_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(TITLE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(TITLE_AUTHOR_PANELLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(AUTHOR))
        );
        TITLE_AUTHOR_PANELLayout.setVerticalGroup(
            TITLE_AUTHOR_PANELLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(TITLE_AUTHOR_PANELLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(TITLE, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(AUTHOR))
        );

        LOAD_STOPLIST.setText("Load list...");
        LOAD_STOPLIST.setName("SAVEEXCLUDE"); // NOI18N
        LOAD_STOPLIST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                LOAD_STOPLISTActionPerformed(evt);
            }
        });

        jButton2.setText("TUTORIAL");

        jButton3.setText("Tips!");

        javax.swing.GroupLayout MAIN_PANEL_1Layout = new javax.swing.GroupLayout(MAIN_PANEL_1);
        MAIN_PANEL_1.setLayout(MAIN_PANEL_1Layout);
        MAIN_PANEL_1Layout.setHorizontalGroup(
            MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(STOPLIST, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(REMOVE_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(NEW_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(SAVE_STOPLIST, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(LOAD_STOPLIST, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(ADD_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(TITLE_AUTHOR_PANEL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(WORDSNOTCOUNTED, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(jButton3)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        MAIN_PANEL_1Layout.setVerticalGroup(
            MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addComponent(TITLE_AUTHOR_PANEL, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(WORDSNOTCOUNTED, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addGap(5, 5, 5)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton3)))
                .addGroup(MAIN_PANEL_1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(STOPLIST, javax.swing.GroupLayout.PREFERRED_SIZE, 391, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(MAIN_PANEL_1Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addComponent(NEW_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ADD_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(74, 74, 74)
                        .addComponent(REMOVE_STOPWORD, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SAVE_STOPLIST)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(LOAD_STOPLIST)
                        .addGap(36, 36, 36))))
        );

        DELETE_ENTRY.setLabel("DELETE");
        DELETE_ENTRY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                DELETE_ENTRYActionPerformed(evt);
            }
        });

        NEW_ENTRY.setLabel("NEW");
        NEW_ENTRY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                NEW_ENTRYActionPerformed(evt);
            }
        });

        SKILLS_ENTRY.setText("Copy/paste a job's Qualifications or Preferred Skills list w/ one item per line\n- List item 1\n- List item 2\n         ...\n         ...\n         ...\n- Final list item\n\n(Symbols and other non-letter/number characters are ignored)");

        SAVE_ENTRY.setLabel("SAVE");
        SAVE_ENTRY.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVE_ENTRYActionPerformed(evt);
            }
        });

        ENTRY_NAME.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        ENTRY_NAME.setText("Position Name");
        ENTRY_NAME.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ENTRY_NAMEActionPerformed(evt);
            }
        });

        ENTRY_LIST.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        ENTRY_LIST.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                ENTRY_LISTItemStateChanged(evt);
            }
        });

        POSTINGS_LABEL.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        POSTINGS_LABEL.setText("Job Entries:");

        javax.swing.GroupLayout MAIN_PANEL_2Layout = new javax.swing.GroupLayout(MAIN_PANEL_2);
        MAIN_PANEL_2.setLayout(MAIN_PANEL_2Layout);
        MAIN_PANEL_2Layout.setHorizontalGroup(
            MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                        .addComponent(DELETE_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(NEW_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(141, 141, 141)
                        .addComponent(SAVE_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, 198, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                        .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(ENTRY_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(POSTINGS_LABEL, javax.swing.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SKILLS_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, 421, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ENTRY_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 368, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MAIN_PANEL_2Layout.setVerticalGroup(
            MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(ENTRY_NAME, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(POSTINGS_LABEL, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3)
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(ENTRY_LIST, javax.swing.GroupLayout.DEFAULT_SIZE, 186, Short.MAX_VALUE)
                    .addComponent(SKILLS_ENTRY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(MAIN_PANEL_2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(DELETE_ENTRY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(NEW_ENTRY, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(SAVE_ENTRY, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        OCCURRENCE_HEADER.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        OCCURRENCE_HEADER.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        OCCURRENCE_HEADER.setText("Word Occurrence:");
        OCCURRENCE_HEADER.setName("WORDBANNER"); // NOI18N

        ANALYZE.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        ANALYZE.setText("ANALYZE");
        ANALYZE.setName("ANALYZE"); // NOI18N
        ANALYZE.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ANALYZEActionPerformed(evt);
            }
        });

        SINGLES.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        TRIPLETS.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        PAIRS.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        SAVE_RESULTS.setText("Save Results");
        SAVE_RESULTS.setName("SAVEEXCLUDE"); // NOI18N
        SAVE_RESULTS.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVE_RESULTSActionPerformed(evt);
            }
        });

        SAVE_JOBLIST.setText("Save Job List");
        SAVE_JOBLIST.setName("SAVEEXCLUDE"); // NOI18N
        SAVE_JOBLIST.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SAVE_JOBLISTActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout MAIN_PANEL_3Layout = new javax.swing.GroupLayout(MAIN_PANEL_3);
        MAIN_PANEL_3.setLayout(MAIN_PANEL_3Layout);
        MAIN_PANEL_3Layout.setHorizontalGroup(
            MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                .addGroup(MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ANALYZE, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(OCCURRENCE_HEADER, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addComponent(SAVE_JOBLIST, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(SAVE_RESULTS, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(SINGLES, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(PAIRS, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(TRIPLETS, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        MAIN_PANEL_3Layout.setVerticalGroup(
            MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(MAIN_PANEL_3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                        .addComponent(TRIPLETS, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(SINGLES, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(MAIN_PANEL_3Layout.createSequentialGroup()
                        .addGap(22, 22, 22)
                        .addComponent(OCCURRENCE_HEADER)
                        .addGap(18, 18, 18)
                        .addComponent(ANALYZE, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(SAVE_JOBLIST)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(SAVE_RESULTS)
                        .addGap(23, 23, 23))
                    .addComponent(PAIRS, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(MAIN_PANEL_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(MAIN_PANEL_2, javax.swing.GroupLayout.PREFERRED_SIZE, 586, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(MAIN_PANEL_3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(MAIN_PANEL_2, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(MAIN_PANEL_3, javax.swing.GroupLayout.PREFERRED_SIZE, 206, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(MAIN_PANEL_1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void ANALYZEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ANALYZEActionPerformed
        analyze();
    }//GEN-LAST:event_ANALYZEActionPerformed

    private void REMOVE_STOPWORDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_REMOVE_STOPWORDActionPerformed
        removeStopWord();
    }//GEN-LAST:event_REMOVE_STOPWORDActionPerformed

    private void ADD_STOPWORDActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ADD_STOPWORDActionPerformed
        addStopWord();
    }//GEN-LAST:event_ADD_STOPWORDActionPerformed

    private void SAVE_STOPLISTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVE_STOPLISTActionPerformed
        popMode = "saveStoplist";
        popOpen(popMode);
    }//GEN-LAST:event_SAVE_STOPLISTActionPerformed
/**/
    private void SAVE_RESULTSActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVE_RESULTSActionPerformed
        popMode = "saveResults";
        popOpen(popMode);    }//GEN-LAST:event_SAVE_RESULTSActionPerformed

    private void DELETE_ENTRYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_DELETE_ENTRYActionPerformed
        int temp = ENTRY_LIST.getSelectedIndex();
        for (Entry entry : entryList) {
            if (entry.id > temp)
                entry.id = entry.id - 1;
        }
        entryList.remove(temp);
        ENTRY_LIST.remove(temp);
        Entry entry = new Entry();
        ENTRY_NAME.setText("Position Name");
        SKILLS_ENTRY.setText(null);
        currentEntry = entry;
        ENTRY_LIST.deselect(ENTRY_LIST.getSelectedIndex());
    }//GEN-LAST:event_DELETE_ENTRYActionPerformed

    private void SAVE_ENTRYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVE_ENTRYActionPerformed
        if(ENTRY_NAME.getText().equals("")){
            currentEntry.name = "Untitled Job";
        }else
            currentEntry.name = ENTRY_NAME.getText();
        
        if(SKILLS_ENTRY.getText().equals("")){
            SKILLS_ENTRY.setText("Error: Empty job posting. Please enter at least one line.");
        }
        else{
            currentEntry.skills = SKILLS_ENTRY.getText();
            if(currentEntry.id == -1){
                currentEntry.id = entryList.size();
                entryList.add(currentEntry);
                ENTRY_LIST.add(currentEntry.name);
            }
            else{
                entryList.set(currentEntry.id, currentEntry);
                ENTRY_LIST.getSelectedItem().equals(currentEntry.name);
            }
            Entry entry = new Entry();
            ENTRY_NAME.setText("Position Name");
            SKILLS_ENTRY.setText(null);
            currentEntry = entry;
            ENTRY_LIST.deselect(ENTRY_LIST.getSelectedIndex());
        }
    }//GEN-LAST:event_SAVE_ENTRYActionPerformed

    private void NEW_ENTRYActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_NEW_ENTRYActionPerformed
        Entry entry = new Entry();
        ENTRY_NAME.setText("Position Name");
        SKILLS_ENTRY.setText(null);
        currentEntry = entry;
        ENTRY_LIST.deselect(ENTRY_LIST.getSelectedIndex());
    }//GEN-LAST:event_NEW_ENTRYActionPerformed

    private void ENTRY_LISTItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_ENTRY_LISTItemStateChanged
        currentEntry = entryList.get(ENTRY_LIST.getSelectedIndex());
        ENTRY_NAME.setText(currentEntry.name);
        SKILLS_ENTRY.setText(currentEntry.skills);
    }//GEN-LAST:event_ENTRY_LISTItemStateChanged

    private void ENTRY_NAMEActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ENTRY_NAMEActionPerformed
        if(ENTRY_NAME.getText().equals("Position Name"))
            ENTRY_NAME.setText("");
    }//GEN-LAST:event_ENTRY_NAMEActionPerformed

    private void SAVE_JOBLISTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SAVE_JOBLISTActionPerformed
        popMode = "saveJoblist";
        popOpen(popMode);        popOpen(popMode);    }//GEN-LAST:event_SAVE_JOBLISTActionPerformed

    private void POPOKActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_POPOKActionPerformed
        POPUP.setVisible(false);
        popFx(popMode);
    }//GEN-LAST:event_POPOKActionPerformed

    private void LOAD_STOPLISTActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_LOAD_STOPLISTActionPerformed
        popMode = "loadStoplist";
        popOpen(popMode);    }//GEN-LAST:event_LOAD_STOPLISTActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Wordbuzzer.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Wordbuzzer().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button ADD_STOPWORD;
    private javax.swing.JButton ANALYZE;
    private javax.swing.JLabel AUTHOR;
    private java.awt.Button DELETE_ENTRY;
    private java.awt.List ENTRY_LIST;
    private java.awt.TextField ENTRY_NAME;
    private javax.swing.JButton LOAD_STOPLIST;
    private javax.swing.JPanel MAIN_PANEL_1;
    private javax.swing.JPanel MAIN_PANEL_2;
    private javax.swing.JPanel MAIN_PANEL_3;
    private java.awt.Button NEW_ENTRY;
    private java.awt.TextField NEW_STOPWORD;
    private javax.swing.JLabel OCCURRENCE_HEADER;
    private java.awt.List PAIRS;
    private javax.swing.JLabel POPLABEL;
    private javax.swing.JButton POPOK;
    private java.awt.TextArea POPTEXT;
    private javax.swing.JDialog POPUP;
    private javax.swing.JLabel POPWARNING;
    private java.awt.Label POSTINGS_LABEL;
    private java.awt.Button REMOVE_STOPWORD;
    private java.awt.Button SAVE_ENTRY;
    private javax.swing.JButton SAVE_JOBLIST;
    private javax.swing.JButton SAVE_RESULTS;
    private javax.swing.JButton SAVE_STOPLIST;
    private java.awt.List SINGLES;
    private java.awt.TextArea SKILLS_ENTRY;
    private java.awt.List STOPLIST;
    private javax.swing.JLabel TITLE;
    private javax.swing.JPanel TITLE_AUTHOR_PANEL;
    private java.awt.List TRIPLETS;
    private javax.swing.JLabel WORDSNOTCOUNTED;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    // End of variables declaration//GEN-END:variables
}

