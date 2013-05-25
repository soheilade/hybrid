/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btc;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Variable;
import org.semanticweb.yars.nx.parser.NxParser;

/**
 *
 * @author sohdeh
 */
public class index {

    HashMap<Node, HashMap<Node, String>> SP = new HashMap<Node, HashMap<Node, String>>();
    HashMap<Node, String> P = new HashMap<Node, String>();
    HashMap<Node, HashMap<Node, String>> OP = new HashMap<Node, HashMap<Node, String>>();
    Map<Node, List<Node[]>> tpS = new HashMap<Node, List<Node[]>>();
    Map<Node, List<Node[]>> tpO = new HashMap<Node, List<Node[]>>();

    public static void main(String[] args) {
        index s = new index();
        //build index
        s.BuildIndex("G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq");
        //extract all existing triple patterns and seprate them based on variable name and position
        s.extractAlltriplePatterns("G:/josirefs/hybridsparql/querylog/d.csv");
        //process s-s join and compute its freshness based on index
        System.out.println("find all O-O joins among triple patterns");
        s.AVGOOUsingIndex();
        System.out.println("find all S-O joins among triple patterns");
        s.AVGSOUsingIndex();
        System.out.println("find all S-S joins among triple patterns");
        s.AVGSSUsingIndex();
    }

    public void BuildIndex(String filePath) {
        double f_value = 0;
        try {
            InputStream is = new FileInputStream(filePath);
            NxParser nxp = new NxParser(is);
            while (nxp.hasNext()) {
                Node[] next = nxp.next();
                insertToSP(next);
                //insertToPO(next);//we never have intermediate results based on P
                insertToP(next);//but we need aggregate predicate freshness
                //insertToOS(next);//we never need to extract intermediate O based on S
                insertToOP(next);//we for o-o join and part of s-o join we need to extract intermediate O based on p
            }
            aggregateIndex();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void insertToP(Node[] next){
        String predicateStat= (String) P.get(next[1]);
        if (predicateStat == null) {
            if (next[3].toString().equalsIgnoreCase("true")) {
                P.put(next[1], "1,1");
            } else {
                P.put(next[1], "0,1");
            }
        } else {
                String[] str = predicateStat.split(",");
                int t = Integer.parseInt(str[1]);
                t++;
                int f = Integer.parseInt(str[0]);
                if (next[3].toString().equalsIgnoreCase("true")) {
                    P.put(next[1], String.valueOf(f + 1) + "," + String.valueOf(t));
                } else {
                    P.put(next[1], String.valueOf(f) + "," + String.valueOf(t));
                }
            }
        }
    public void insertToOP(Node[] next) {
        HashMap predicateList = (HashMap) OP.get(next[2]);
        if (predicateList == null) {
            predicateList = new HashMap();
            if (next[3].toString().equalsIgnoreCase("true")) {
                predicateList.put(next[1], "1,1");
            } else {
                predicateList.put(next[1], "0,1");
            }
            //OP.put(next[2], predicateList);
        } else {
            Object predStat = predicateList.get(next[1]);
            if (predStat == null) {
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[1], "1,1");
                } else {
                    predicateList.put(next[1], "0,1");
                }
            } else {
                String[] str = predStat.toString().split(",");
                int t = Integer.parseInt(str[1]);
                t++;
                int f = Integer.parseInt(str[0]);
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[1], String.valueOf(f + 1) + "," + String.valueOf(t));
                } else {
                    predicateList.put(next[1], String.valueOf(f) + "," + String.valueOf(t));
                }
            }            
        }
        OP.put(next[2], predicateList);
    }
    public void insertToSP(Node[] next) {
        HashMap predicateList = (HashMap) SP.get(next[0]);
        if (predicateList == null) {
            predicateList = new HashMap();
            if (next[3].toString().equalsIgnoreCase("true")) {
                predicateList.put(next[1], "1,1");
            } else {
                predicateList.put(next[1], "0,1");
            }
            //SP.put(next[0], predicateList);
        } else {
            Object predStat = predicateList.get(next[1]);
            if (predStat == null) {
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[1], "1,1");
                } else {
                    predicateList.put(next[1], "0,1");
                }
            } else {
                String[] str = predStat.toString().split(",");
                int t = Integer.parseInt(str[1]);
                t++;
                int f = Integer.parseInt(str[0]);
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[1], String.valueOf(f + 1) + "," + String.valueOf(t));
                } else {
                    predicateList.put(next[1], String.valueOf(f) + "," + String.valueOf(t));
                }
            }
        }
        SP.put(next[0], predicateList);
    }
    public void insertToPO(Node[] next) {
        HashMap predicateList = (HashMap) PO.get(next[1]);
        if (predicateList == null) {
            predicateList = new HashMap();
            if (next[3].toString().equalsIgnoreCase("true")) {
                predicateList.put(next[2], "1,1");
            } else {
                predicateList.put(next[2], "0,1");
            }
            PO.put(next[1], predicateList);
        } else {
            Object predStat = predicateList.get(next[1]);
            if (predStat == null) {
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[2], "1,1");
                } else {
                    predicateList.put(next[2], "0,1");
                }
            } else {
                String[] str = predStat.toString().split(",");
                int t = Integer.parseInt(str[1]);
                t++;
                int f = Integer.parseInt(str[0]);
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[2], String.valueOf(f + 1) + "," + String.valueOf(t));
                } else {
                    predicateList.put(next[2], String.valueOf(f) + "," + String.valueOf(t));
                }
            }
        }
    }
    public void insertToOS(Node[] next) {
        HashMap predicateList = (HashMap) OS.get(next[2]);
        if (predicateList == null) {
            predicateList = new HashMap();
            if (next[3].toString().equalsIgnoreCase("true")) {
                predicateList.put(next[0], "1,1");
            } else {
                predicateList.put(next[0], "0,1");
            }
            OS.put(next[2], predicateList);
        } else {
            Object predStat = predicateList.get(next[1]);
            if (predStat == null) {
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[0], "1,1");
                } else {
                    predicateList.put(next[0], "0,1");
                }
            } else {
                String[] str = predStat.toString().split(",");
                int t = Integer.parseInt(str[1]);
                t++;
                int f = Integer.parseInt(str[0]);
                if (next[3].toString().equalsIgnoreCase("true")) {
                    predicateList.put(next[0], String.valueOf(f + 1) + "," + String.valueOf(t));
                } else {
                    predicateList.put(next[0], String.valueOf(f) + "," + String.valueOf(t));
                }
            }
        }
    }
    public void aggregateIndex() {
        Iterator spit = SP.keySet().iterator();
        Iterator poit = PO.keySet().iterator();
        Iterator osit = OS.keySet().iterator();
        while (spit.hasNext()) {
            Node curSub = (Node) spit.next();
            HashMap predList = (HashMap) SP.get(curSub);
            Iterator predIt = predList.keySet().iterator();
            int tsum = 0, fsum = 0;
            double freshratiosum = 0, count = 0;
            while (predIt.hasNext()) {
                count++;
                Node predNode = (Node) predIt.next();
                String SPStat = predList.get(predNode).toString();
                fsum += Integer.valueOf(SPStat.split(",")[0]);
                tsum += Integer.valueOf(SPStat.split(",")[1]);
                freshratiosum += Double.valueOf(SPStat.split(",")[0]) / Double.valueOf(SPStat.split(",")[1]);
            }
            predList.put("objAvg", String.valueOf(fsum) + "," + String.valueOf(tsum) + "," + String.valueOf(freshratiosum / count));
        }
        while (poit.hasNext()) {
            Node curPred = (Node) poit.next();
            HashMap ObjList = (HashMap) PO.get(curPred);
            Iterator ObjIt = ObjList.keySet().iterator();
            int tsum = 0, fsum = 0;
            double freshratiosum = 0, count = 0;
            while (ObjIt.hasNext()) {
                count++;
                Node predNode = (Node) ObjIt.next();
                String SPStat = ObjList.get(predNode).toString();
                fsum += Integer.valueOf(SPStat.split(",")[0]);
                tsum += Integer.valueOf(SPStat.split(",")[1]);
                freshratiosum += Double.valueOf(SPStat.split(",")[0]) / Double.valueOf(SPStat.split(",")[1]);
            }
            ObjList.put("SubjAvg", String.valueOf(fsum) + "," + String.valueOf(tsum) + "," + String.valueOf(freshratiosum / count));
        }
        while (osit.hasNext()) {
            Node curObj = (Node) osit.next();
            HashMap SubjList = (HashMap) OS.get(curObj);
            Iterator SubjIt = SubjList.keySet().iterator();
            int tsum = 0, fsum = 0;
            double freshratiosum = 0, count = 0;
            while (SubjIt.hasNext()) {
                count++;
                Node predNode = (Node) SubjIt.next();
                String SPStat = SubjList.get(predNode).toString();
                fsum += Integer.valueOf(SPStat.split(",")[0]);
                tsum += Integer.valueOf(SPStat.split(",")[1]);
                freshratiosum += Double.valueOf(SPStat.split(",")[0]) / Double.valueOf(SPStat.split(",")[1]);
            }
            SubjList.put("PredAvg", String.valueOf(fsum) + "," + String.valueOf(tsum) + "," + String.valueOf(freshratiosum / count));
        }
    }
    public void extractAlltriplePatterns(String filePath) {
        try {
            InputStream qin = new FileInputStream(new File(filePath));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void AVGSSUsingIndex() {
        Iterator tpit = tpS.keySet().iterator();
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
                double realf = computeRealSSJoinFreshness(curtp, prevtp);
                //SSIndexLookUpAvg(prevtp, curtp);
                //iterate through SP compute tps normalized freshness actually here we compute intermediate results from index
                Iterator itsp = SP.keySet().iterator();
                double sumprev = 0, sumcur = 0;
                double countprev = 0, countcur = 0;
                while (itsp.hasNext()) {
                    //curtp
                    Node temps = (Node) itsp.next();
                    HashMap temppl = (HashMap) SP.get(temps);
                    Object predStat = temppl.get(curtp[1]);
                    if (predStat != null) {
                        String[] strs = predStat.toString().split(",");
                        sumcur += Double.parseDouble(strs[0]) / Double.parseDouble(strs[1]);
                        countcur++;
                    }
                    //prevtp
                    predStat = temppl.get(prevtp[1]);
                    if (predStat != null) {
                        String[] strs = predStat.toString().split(",");
                        sumprev += Double.parseDouble(strs[0]) / Double.parseDouble(strs[1]);
                        countprev++;
                    }
                }
                double CurfAverage = sumcur / countcur;
                double prevfAverage = sumprev / countprev;
                //compute tps real freshness according to predicate [might be biased]                
                String strprev = P.get(prevtp[1]).toString();
                double prevfReal = Double.parseDouble(strprev.split(",")[0]) / Double.parseDouble(strprev.split(",")[1]);
                String strcur = P.get(curtp[1]).toString();
                double curfReal = Double.parseDouble(strcur.split(",")[0]) / Double.parseDouble(strcur.split(",")[1]);
                System.out.println(prevtp[0].toN3() + "," + prevtp[1].toN3() + "," + prevtp[2].toN3()+"," + curtp[0].toN3() + "," + curtp[1].toN3() + "," + curtp[2].toN3() + "," + prevfReal + ","+ strprev.split(",")[1]+","+ curfReal + "," +strcur.split(",")[1]+ ","+prevfReal * curfReal + "," + prevfAverage + ","+countprev+","+ CurfAverage + ","+countcur+"," + prevfAverage * CurfAverage );
            }
        }
    }
    public double computeRealSSJoinFreshness(Node[] curtp, Node[] prevtp) {
        double res = 0;
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
        HashMap phm1 = new HashMap();//entries that matches cur tp
        HashMap phm2 = new HashMap();//entries that matches prev tp
        Double f_value1 = 0.0;
        Double t_value1 = 0.0;
        Double f_value2 = 0.0;
        Double t_value2 = 0.0;
        try {//fill 2 hashmaps from dataset with instances of triple patterns that we want to join , we can compute f-value here since we need 
            //to iterate over the whole dataset to to fill hashmaps anyway
            InputStream isi = new FileInputStream("G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq");
            NxParser nxpi = new NxParser(isi);//extracting triples that matches triple pattern
            int i=0;
            while (nxpi.hasNext()) {
                i++;
                Node[] nexta = nxpi.next();
                //check it that triple matches curent tp
                if (var.compare(nexta, curtp) == 0) {
                    if (nexta[3].toString().trim().equalsIgnoreCase("false")) {
                        f_value1++;
                    }
                    else if (nexta[3].toString().trim().equalsIgnoreCase("true")) {
                        t_value1++;
                    }
                    phm1.put(nexta, nexta[0]);
                }
                //check it that triple matches previouse tp
                if (var.compare(nexta, prevtp) == 0) {
                    if (nexta[3].toString().trim().equalsIgnoreCase("false")) {
                        f_value2++;
                    }
                    else if (nexta[3].toString().trim().equalsIgnoreCase("true")) {
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
            System.out.print(fresh2 + ","+total2+"," + fresh1 + ","+total1+"," + freshj+","+totalj+",");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public double computeRealOOJoinFreshness(Node[] curtp, Node[] prevtp) {
        double res = 0;
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
        HashMap phm1 = new HashMap();//entries that matches o1
        HashMap phm2 = new HashMap();//entries that matches o2
        Double f_value1 = 0.0;
        Double t_value1 = 0.0;
        Double f_value2 = 0.0;
        Double t_value2 = 0.0;
        try {//fill 2 hashmaps from dataset with instances of triple patterns that we want to join , we can compute f-value here since we need 
            //to iterate over the whole dataset to to fill hashmaps anyway
            InputStream isi = new FileInputStream("G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq");
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
                    if (fpi[2].toString().trim().equalsIgnoreCase(spi[2].toString().trim())) {//since it is an O-O join
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
            System.out.print(fresh1 + ","+total1+"," + fresh2 + ","+total2+"," + freshj+","+totalj+",");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public double computeRealSOJoinFreshness(Node[] curtp,Node[] prevtp) {
        double res = 0;
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
        HashMap phm1 = new HashMap();//entries that matches o1
        HashMap phm2 = new HashMap();//entries that matches o2
        Double f_value1 = 0.0;
        Double t_value1 = 0.0;
        Double f_value2 = 0.0;
        Double t_value2 = 0.0;
        try {//fill 2 hashmaps from dataset with instances of triple patterns that we want to join , we can compute f-value here since we need 
            //to iterate over the whole dataset to to fill hashmaps anyway
            InputStream isi = new FileInputStream("G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq");
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
                    if (fpi[0].toString().trim().equalsIgnoreCase(spi[2].toString().trim())) {//since it is an S-O join
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
            System.out.print(fresh1 + ","+total1+"," + fresh2 + ","+total2+"," + freshj+","+totalj+",");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;
    }
    public void AVGOOUsingIndex() {
        Iterator tpit = tpO.keySet().iterator();
        //iterating through all subjects and their correspoding list of tps
        while (tpit.hasNext()) {
            Node CurObj;
            //current subject that we are gonna extract all its tps and join them
            CurObj = (Node) tpit.next();
            //get the corresponding arraylist of all tps that have this subject
            ArrayList<Node[]> tpswithcommonO = (ArrayList<Node[]>) tpO.get(CurObj);
            Node[] curtp = new Node[3], prevtp = new Node[3];
            //iterate through array list and join them pairwise
            for (int c = 0; c < tpswithcommonO.size(); c++) {
                prevtp = curtp.clone();
                curtp = (Node[]) tpswithcommonO.get(c);
                if (prevtp[0] == null) {
                    continue;
                }
                double realf = computeRealOOJoinFreshness(curtp, prevtp);
                //iterate through SP compute tps normalized freshness in fact, here we compute intermediate results from index and then compute average over intermediate results
                Iterator itop = OP.keySet().iterator();
                double sumprev = 0, sumcur = 0;
                double countprev = 0, countcur = 0;
                while (itop.hasNext()) {
                    //computing curtp freshness from OP using intermediate results
                    Node tempo = (Node) itop.next();
                    HashMap temppl = (HashMap) OP.get(tempo);
                    Object predStat = temppl.get(curtp[1]);
                    if (predStat != null) {
                        String[] strs = predStat.toString().split(",");
                        sumcur += Double.parseDouble(strs[0]) / Double.parseDouble(strs[1]);
                        countcur++;
                    }
                    //computing prevtp freshness from OP using intermediate results
                    predStat = temppl.get(prevtp[1]);
                    if (predStat != null) {
                        String[] strs = predStat.toString().split(",");
                        sumprev += Double.parseDouble(strs[0]) / Double.parseDouble(strs[1]);
                        countprev++;
                    }
                }
                double CurfAverage = sumcur / countcur;
                double prevfAverage = sumprev / countprev;
                //compute tps real freshness [might be biased] in fact this is just simple multiplication of each predicate freshness  based on common pred we need PS index or we can compute using SP index              
                String strprev = P.get(prevtp[1]).toString();
                double prevfReal = Double.parseDouble(strprev.split(",")[0]) / Double.parseDouble(strprev.split(",")[1]);
                String strcur = P.get(curtp[1]).toString();
                double curfReal = Double.parseDouble(strcur.split(",")[0]) / Double.parseDouble(strcur.split(",")[1]);
                System.out.println(prevtp[0].toN3() + "," + prevtp[1].toN3() + "," + prevtp[2].toN3()+"," + curtp[0].toN3() + "," + curtp[1].toN3() + "," + curtp[2].toN3() + "," + prevfReal + ","+ strprev.split(",")[1]+","+ curfReal + "," +strcur.split(",")[1]+ ","+prevfReal * curfReal + "," + prevfAverage + ","+countprev+","+ CurfAverage + ","+countcur+"," + prevfAverage * CurfAverage );
            }
        }
    }
    public void AVGSOUsingIndex() {
        Iterator otpit = tpO.keySet().iterator();        
        //iterating through all subjects and their correspoding list of tps
        while (otpit.hasNext()) {
            Node CurObj;
            //current subject that we are gonna extract all its tps and join them
            CurObj = (Node) otpit.next();
            Iterator stpit = tpS.keySet().iterator();
            while (stpit.hasNext()) {
                Node CurSubj;
                CurSubj = (Node) stpit.next();
                if (CurObj.toString().equalsIgnoreCase(CurSubj.toString())) {
                    //get the corresponding arraylist of all tps that have this subject
                    ArrayList<Node[]> tpswithcommonO = (ArrayList<Node[]>) tpO.get(CurObj);
                    ArrayList<Node[]> tpswithcommonS = (ArrayList<Node[]>) tpS.get(CurSubj);
                    Node[] otp = new Node[3], stp = new Node[3];
                    //iterate through tpo with common O
                    for (int oc = 0; oc < tpswithcommonO.size(); oc++) {
                        otp = (Node[]) tpswithcommonO.get(oc);
                        //iterate through tpo with common O
                        for (int sc = 0; sc < tpswithcommonS.size(); sc++) {
                            stp = (Node[]) tpswithcommonS.get(sc);
                            double realf = computeRealSOJoinFreshness(stp,otp);                
                            //iterate through SP compute tps normalized freshness in fact, here we compute intermediate results from index and then compute average over intermediate results
                            Iterator itsp = SP.keySet().iterator();
                            double sums = 0, sumo = 0;
                            double counts = 0, counto = 0;
                            while (itsp.hasNext()) {
                                //stp
                                Node temps = (Node) itsp.next();
                                HashMap temppl = (HashMap) SP.get(temps);
                                Object predStat = temppl.get(stp[1]);
                                if (predStat != null) {
                                    String[] strs = predStat.toString().split(",");
                                    sums += Double.parseDouble(strs[0]) / Double.parseDouble(strs[1]);
                                    counts++;
                                }
                            }
                            Iterator itop = OP.keySet().iterator();
                            while (itop.hasNext()) {
                                //otp
                                Node tempo = (Node) itop.next();
                                HashMap temppl = (HashMap) OP.get(tempo);
                                Object predStat = temppl.get(otp[1]);
                                if (predStat != null) {
                                    String[] strs = predStat.toString().split(",");
                                    sumo += Double.parseDouble(strs[0]) / Double.parseDouble(strs[1]);
                                    counto++;
                                }
                            }
                            //now we can compute normalized of stp and otp
                            double sfAverage = sums / counts;
                            double ofAverage = sumo / counto;
                            //compute tps real freshness [might be biased] in fact this is just simple multiplication of each predicate freshness                
                            String strprev = P.get(stp[1]).toString();
                            double sfReal = Double.parseDouble(strprev.split(",")[0]) / Double.parseDouble(strprev.split(",")[1]);
                            String strcur = P.get(otp[1]).toString();
                            double ofReal = Double.parseDouble(strcur.split(",")[0]) / Double.parseDouble(strcur.split(",")[1]);
                            System.out.println(stp[0].toN3() + "," + stp[1].toN3() + "," + stp[2].toN3() +","+ otp[0].toN3() + "," + otp[1].toN3() + "," + otp[2].toN3() + "," + sfReal + ","+ strprev.split(",")[1]+","+ ofReal + "," +strcur.split(",")[1]+ ","+sfReal * sfReal + "," + sfAverage + ","+counts+","+ ofAverage + ","+counto+"," + sfAverage * ofAverage );
                            //System.out.println(stp[0].toString() + "," + stp[1].toString() + "," + stp[2].toString() + ">>JION<<"
                            //        + otp[0].toString() + "," + otp[1].toString() + "," + otp[2].toString() + "real(" + sfReal + "*" + ofReal + "=" + sfReal * ofReal + ") normalized(" + sfAverage + "*" + ofAverage + "=" + sfAverage * ofAverage + ")");
                        }
                    }
                }
            }
        }
    }
}
