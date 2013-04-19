
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btc;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Random;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;

/**
 *
 * @author sohdeh
 */
public class simulateupdate {
    public static void main(String[] args) {
        try {
            OutputStream os = new FileOutputStream("G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp.nq");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            InputStream is = new FileInputStream("G://josirefs/hybridsparql/bsbmtools-0.2/dataset.n3");
            BufferedReader br=new BufferedReader(new FileReader(new File("G://josirefs/hybridsparql/bsbmtools-0.2/preds.txt")));
            ArrayList preds=new ArrayList();
            for (int i=0;i<40;i++) preds.add(br.readLine());
            br.close();
            
            Random r = new Random();
            Random rpercent = new Random(10);
            int[] buckets = new int[]{8,4,2,3,4,2,1,2,3,3,8};//obtained from beta distribution
            for(int j=0;j<buckets.length;j++){
                int count=buckets[j];
                //count is the number of predicates to be chosen randomly from preds.txt file to assign their freshness according to j% therefore in each iteration
                // we generate a random number to specify the index of predicate
                for(int i=0;i<count;i++){
                    int prednum = r.nextInt(preds.size());
                    String str = preds.get(prednum).toString();  
                    preds.remove(prednum);
                    is = new FileInputStream("G://josirefs/hybridsparql/bsbmtools-0.2/dataset.n3");
                    NxParser nxp = new NxParser(is);
                    while (nxp.hasNext()) {
                        Node[] next = nxp.next();
                        if (str.equalsIgnoreCase(next[1].toString().trim()) ) 
                            if(rpercent.nextInt()==j){
                            bw.write(next[0].toN3() + " " + next[1].toN3() + " " + next[2].toN3() + " " + "\"true\" ." + "\n");
                            }else
                            {bw.write(next[0].toN3() + " " + next[1].toN3() + " " + next[2].toN3() + " " + "\"false\" ." + "\n");}
                    }                    
                }
            }
            bw.flush();
            bw.close();
        }catch(Exception e){e.printStackTrace();}
}
}
