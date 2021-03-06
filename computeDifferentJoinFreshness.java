/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btc;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.reasoner.TriplePattern;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.NodeComparator;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.Triple;
import org.semanticweb.yars.nx.Variable;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.reorder.ReorderIterator;

/**
 *
 * @author sohdeh
 */

public class simulatedJoin {

    public static void main(String[] args) {
        try {
            Comparator<Node[]> var = new Comparator<Node[]>() {
                public int compare(Node[] o1, Node[] o2) {
                    int diff = 0;
                    //just need to compare predicate=2 and object=3 because we are matching subject
                    for (int i = 0; i < 3; i++) {
                        if (o2[i] instanceof Variable || o1[i] instanceof Variable) {
                            continue;
                        }
                        diff = o1[i].toString().trim().compareTo(o2[i].toString().trim());
                        if (diff != 0) {
                            return diff;
                        }
                    }
                    return diff;
                }
            };
            Comparator<Node[]> vartp = new Comparator<Node[]>() {
                public int compare(Node[] o1, Node[] o2) {
                    int diff = 0;
                    //just need to compare predicate=2 and object=3 because we are matching subject
                    for (int i = 0; i < 3; i++) {
                        if (o2[i] instanceof Variable && o1[i] instanceof Variable) {
                            continue;
                        }
                        diff = o1[i].toString().trim().compareTo(o2[i].toString().trim());
                        if (diff != 0) {
                            return diff;
                        }
                    }
                    return diff;
                }
            };
            String nxpath = "G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq";
            statistics s = new statistics();
            Map<Node, List<Node[]>> tpS = new HashMap<Node, List<Node[]>>();
            //Map<Node[], Double> tpP = new HashMap<Node[], Double>();
            Map<Node, List<Node[]>> tpO = new HashMap<Node, List<Node[]>>();
            //read triple pattern file which consists only unique tps
            InputStream qin = new FileInputStream(new File("G:/josirefs/hybridsparql/querylog/d.csv"));
            NxParser nxq = new NxParser(qin);
            Node[] cur = new Node[3];
            while (nxq.hasNext())//foreach triple pattern
            {
                cur = (Node[]) nxq.next();
                //if subject is variable add it to tpS which represents only triples that have a variable in subject position
                if (cur[0] instanceof Variable) {
                    List<Node[]> a = tpS.get(cur[0]);
                    if (a == null) {
                        a = new ArrayList<>();
                    }
                    a.add(cur);
                    tpS.put(cur[0], a);
                }
                //if object is variable add it to tpO which represents only triples that have a variable in object position
                if (cur[2] instanceof Variable) {
                    List<Node[]> a = tpO.get(cur[2]);
                    if (a == null) {
                        a = new ArrayList<>();
                    }
                    a.add(cur);
                    tpO.put(cur[2], a);
                }
            }
            //now we have all the triple patterns seprated based on variable position(subject,object,predicate) we should start to join them
            //first reordering lists
            ////////int[] maskS = {0, 2, 1};
            ////////ReorderIterator riS = new ReorderIterator(tpS.keySet().iterator(), maskS);
            //int[] maskO = {2, 0, 1};
            //ReorderIterator riO = new ReorderIterator(tpO.keySet().iterator(), maskO);
            //find all S-S joins among triple patterns
            System.out.println("find all S-S joins among triple patterns");
            Iterator tpit=tpS.keySet().iterator();
             //iterating through all subjects and their correspoding list of tps
             while (tpit.hasNext()) {
                Node CurSubj;
                //current subject that we are gonna extract all its tps and join them
                CurSubj = (Node) tpit.next();
                //get the corresponding arraylist of all tps that have this subject
                ArrayList<Node[]> tpswithcommonS = (ArrayList<Node[]>) tpS.get(CurSubj);
                Node[] curtp = new Node[3], prevtp = new Node[3];
                //iterate through array list and join them pairwise
                for (int c = 0; c < tpswithcommonS.size(); c++) {
                    prevtp = curtp.clone();
                    curtp = (Node[]) tpswithcommonS.get(c);
                    if (prevtp[0] == null) {
                        continue;
                    }
                    HashMap phm1 = new HashMap();//entries that matches cur tp
                    HashMap phm2 = new HashMap();//entries that matches prev tp
                    Double f_value1 = 0.0;
                    Double t_value1 = 0.0;
                    Double f_value2 = 0.0; 
                    Double t_value2 = 0.0;
                    //fill 2 hashmaps from dataset with instances of triple patterns that we want to join , we can compute f-value here since we need 
                    //to iterate over the whole dataset to to fill hashmaps anyway
                    InputStream isi = new FileInputStream(nxpath);
                    NxParser nxpi = new NxParser(isi);//extracting triples that matches triple pattern
                    while (nxpi.hasNext()) {
                        Node[] nexta = nxpi.next();
                        //check it that triple matches curent tp
                        if (var.compare(nexta, curtp) == 0) {
                            if (nexta[3].toString().trim().equalsIgnoreCase("false")) {
                                f_value1++;
                            }
                            if (nexta[3].toString().trim().equalsIgnoreCase("true")) {
                                t_value1++;
                            }
                            phm1.put(nexta, nexta[0]);
                        }
                        //check it that triple matches previouse tp
                        if (var.compare(nexta, prevtp) == 0) {
                            if (nexta[3].toString().trim().equalsIgnoreCase("false")) {
                                f_value2++;
                            }
                            if (nexta[3].toString().trim().equalsIgnoreCase("true")) {
                                t_value2++;
                            }
                            phm2.put(nexta, nexta[0]);
                        }
                    }
                    //now we have all triples that matches current and previouse tp
                    //perform nested loop join on filled hashmaps
                    Double f_valuej = 0.0;
                    Double t_valuej = 0.0;
                    Iterator pit1 = phm1.keySet().iterator();
                    while (pit1.hasNext()) {
                        Node[] fpi = (Node[]) pit1.next();
                        Iterator pit2 = phm2.keySet().iterator();
                        while (pit2.hasNext()) {
                            Node[] spi = (Node[]) pit2.next();
                            if (fpi[0].toString().trim().equalsIgnoreCase(spi[0].toString().trim())) {//since it is an S-S join
                                boolean b1 = Boolean.parseBoolean(fpi[3].toString());
                                boolean b2 = Boolean.parseBoolean(spi[3].toString());
                                boolean b3 = b1 && b2;
                                if (b3) {
                                    t_valuej++;
                                } else {
                                    f_valuej++;
                                }
                                //bwj.write(fpmatch[0].toString() + " , " + fpmatch[2].toString() + " , " + spmatch[2].toString() + " , " + b3 + "\n");
                            }
                        }
                    }
                    double total1 = t_value1 + f_value1;
                    double fresh1 = t_value1 / total1;
                    double total2 = t_value2 + f_value2;
                    double fresh2 = t_value2 / total2;
                    double totalj = t_valuej + f_valuej;
                    double freshj = t_valuej / totalj;
                    //System.out.println("tp1 "+CurSubj[0].toString()+" "+CurSubj[1].toString()+" "+CurSubj[2].toString()+" results= ("+total1+")= " + fresh1 + " tp2 "+PrevSubj[0].toString()+" "+PrevSubj[1].toString()+" "+PrevSubj[2].toString()+" results= ("+total2+")= " + fresh2 + " tp joint freshness among("+totalj+")= " + freshj);
                    System.out.println(fresh1 + " " + fresh2 + " " + freshj);
                }
            }
            //find all O-O joins
            System.out.println("find all O-O joins among triple patterns");
            Iterator tpito=tpO.keySet().iterator();
             //iterating through all subjects and their correspoding list of tps
             while (tpito.hasNext()) {
                Node Curobj;
                //current subject that we are gonna extract all its tps and join them
                Curobj = (Node) tpito.next();
                //get the corresponding arraylist of all tps that have this subject
                ArrayList<Node[]> tpswithcommono = (ArrayList<Node[]>) tpO.get(Curobj);
                Node[] curtp = new Node[3], prevtp = new Node[3];
                //iterate through array list and join them pairwise
                for (int c = 0; c < tpswithcommono.size(); c++) {
                    prevtp = curtp.clone();
                    curtp = (Node[]) tpswithcommono.get(c);
                    if (prevtp[0] == null) {
                        continue;
                    }
                    HashMap phm1 = new HashMap();//entries that matches cur tp
                    HashMap phm2 = new HashMap();//entries that matches prev tp
                    Double f_value1 = 0.0;
                    Double t_value1 = 0.0;
                    Double f_value2 = 0.0; 
                    Double t_value2 = 0.0;
                    //fill 2 hashmaps from dataset with instances of triple patterns that we want to join , we can compute f-value here since we need 
                    //to iterate over the whole dataset to to fill hashmaps anyway
                    InputStream isi = new FileInputStream(nxpath);
                    NxParser nxpi = new NxParser(isi);//extracting triples that matches triple pattern
                    while (nxpi.hasNext()) {
                        Node[] nexta = nxpi.next();
                        //check it that triple matches curent tp
                        if (var.compare(nexta, curtp) == 0) {
                            if (nexta[3].toString().trim().equalsIgnoreCase("false")) {
                                f_value1++;
                            }
                            if (nexta[3].toString().trim().equalsIgnoreCase("true")) {
                                t_value1++;
                            }
                            phm1.put(nexta, nexta[2]);
                        }
                        //check it that triple matches previouse tp
                        if (var.compare(nexta, prevtp) == 0) {
                            if (nexta[3].toString().trim().equalsIgnoreCase("false")) {
                                f_value2++;
                            }
                            if (nexta[3].toString().trim().equalsIgnoreCase("true")) {
                                t_value2++;
                            }
                            phm2.put(nexta, nexta[2]);
                        }
                    }
                    //now we have all triples that matches current and previouse tp
                    //perform nested loop join on filled hashmaps
                    Double f_valuej = 0.0;
                    Double t_valuej = 0.0;
                    Iterator pit1 = phm1.keySet().iterator();
                    while (pit1.hasNext()) {
                        Node[] fpi = (Node[]) pit1.next();
                        Iterator pit2 = phm2.keySet().iterator();
                        while (pit2.hasNext()) {
                            Node[] spi = (Node[]) pit2.next();
                            if (fpi[2].toString().trim().equalsIgnoreCase(spi[2].toString().trim())) {//since it is an S-S join
                                boolean b1 = Boolean.parseBoolean(fpi[3].toString());
                                boolean b2 = Boolean.parseBoolean(spi[3].toString());
                                boolean b3 = b1 && b2;
                                if (b3) {
                                    t_valuej++;
                                } else {
                                    f_valuej++;
                                }
                                //bwj.write(fpmatch[0].toString() + " , " + fpmatch[2].toString() + " , " + spmatch[2].toString() + " , " + b3 + "\n");
                            }
                        }
                    }
                    double total1 = t_value1 + f_value1;
                    double fresh1 = t_value1 / total1;
                    double total2 = t_value2 + f_value2;
                    double fresh2 = t_value2 / total2;
                    double totalj = t_valuej + f_valuej;
                    double freshj = t_valuej / totalj;
                    //System.out.println("tp1 "+CurSubj[0].toString()+" "+CurSubj[1].toString()+" "+CurSubj[2].toString()+" results= ("+total1+")= " + fresh1 + " tp2 "+PrevSubj[0].toString()+" "+PrevSubj[1].toString()+" "+PrevSubj[2].toString()+" results= ("+total2+")= " + fresh2 + " tp joint freshness among("+totalj+")= " + freshj);
                    System.out.println(fresh1 + " " + fresh2 + " " + freshj);
                }
            }
            ///find all S-O joins 
            System.out.println("find all S-O joins among triple patterns");            
            Iterator stpit = tpS.keySet().iterator();
            Node CurSubjtp = null;
            Node CurObjtp = null;
            while (stpit.hasNext()) {
                CurSubjtp = (Node) stpit.next();
                Iterator otpit = tpO.keySet().iterator();
                while (otpit.hasNext()) {
                    CurObjtp = (Node) otpit.next();
                    if (CurObjtp.toString().equalsIgnoreCase(CurSubjtp.toString()))//we have a S-O Join
                    {
                        ArrayList<Node[]> otparr = (ArrayList<Node[]>) tpO.get(CurObjtp);
                        ArrayList<Node[]> stparr = (ArrayList<Node[]>) tpS.get(CurObjtp);
                        for (int sp = 0; sp < stparr.size(); sp++) {
                            Node[] CurSubjarr = (Node[]) stparr.get(sp);
                            for (int op = 0; op < otparr.size(); op++) {
                                Node[] CurObjarr = (Node[]) otparr.get(op);
                                HashMap phm1 = new HashMap();//entries that matches first tp
                                HashMap phm2 = new HashMap();//entries that matches second tp
                                Double f_value1 = 0.0;//tpS.get(CurSubj);
                                Double t_value1 = 0.0;
                                Double f_value2 = 0.0;// tpS.get(PrevSubj); 
                                Double t_value2 = 0.0;
                                //fill 2 hashmaps from dataset with instances of triple patterns that we want to join , we can compute f-value here since we need 
                                //to iterate over the whole dataset to to fill hashmaps anyway
                                InputStream isi = new FileInputStream(nxpath);
                                NxParser nxpi = new NxParser(isi);//extracting triples that matches triple pattern
                                while (nxpi.hasNext()) {
                                    Node[] nextb = nxpi.next();
                                    if (var.compare(nextb, CurSubjarr) == 0) {
                                        if (nextb[3].toString().trim().equalsIgnoreCase("false")) {
                                            f_value1++;
                                        }
                                        if (nextb[3].toString().trim().equalsIgnoreCase("true")) {
                                            t_value1++;
                                        }
                                        //if(Integer.parseInt(next[2].toString())>3)
                                        phm1.put(nextb, nextb[0]);
                                    }
                                    if (var.compare(nextb, CurObjarr) == 0) {
                                        if (nextb[3].toString().trim().equalsIgnoreCase("false")) {
                                            f_value2++;
                                        }
                                        if (nextb[3].toString().trim().equalsIgnoreCase("true")) {
                                            t_value2++;
                                        }
                                        //if(Integer.parseInt(next[2].toString())>3)
                                        phm2.put(nextb, nextb[2]);
                                    }

                                }
                                //perform nested loop join on filled hashmaps
                                Double f_valuej = 0.0;//tpS.get(CurSubj);
                                Double t_valuej = 0.0;
                                Iterator pit1 = phm1.keySet().iterator();
                                while (pit1.hasNext()) {
                                    Node[] fpi = (Node[]) pit1.next();
                                    Iterator pit2 = phm2.keySet().iterator();
                                    while (pit2.hasNext()) {
                                        Node[] spi = (Node[]) pit2.next();
                                        if (fpi[0].toString().trim().equalsIgnoreCase(spi[2].toString().trim())) {
                                            boolean b1 = Boolean.parseBoolean(fpi[3].toString());
                                            boolean b2 = Boolean.parseBoolean(spi[3].toString());
                                            boolean b3 = b1 && b2;
                                            if (b3) {
                                                t_valuej++;
                                            } else {
                                                f_valuej++;
                                            }
                                            //bwj.write(fpmatch[0].toString() + " , " + fpmatch[2].toString() + " , " + spmatch[2].toString() + " , " + b3 + "\n");
                                        }
                                    }
                                }
                                double fresh1 = f_value1 / (t_value1+f_value1);
                                double fresh2 = f_value2 / (t_value2+f_value2);
                                double freshj = f_valuej / (t_valuej+f_valuej);
                                System.out.println("tp1 = "+CurObjarr[0].toString()+" "+CurObjarr[1].toString()+ " "+CurObjarr[2].toString()+" "+ fresh1 + " tp2 = " + CurSubjarr[0].toString()+" "+CurSubjarr[1].toString()+ " "+CurSubjarr[2].toString()+" "+fresh2 + "tp joint freshness= " + freshj);
                            }

                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
