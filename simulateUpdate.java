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
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
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
            Random r = new Random();
            OutputStream os = new FileOutputStream("G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq");
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(os));
            InputStream is = new FileInputStream("G://josirefs/hybridsparql/bsbmtools-0.2/dataset.n3");
            BufferedReader br = new BufferedReader(new FileReader(new File("G://josirefs/hybridsparql/bsbmtools-0.2/preds.txt")));
            int[] buckets = new int[]{8, 4, 2, 3, 4, 2, 1, 2, 3, 3, 8};//obtained from beta distribution
            List<Integer> list = new LinkedList<Integer>(Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 2, 2, 3, 3, 3, 4, 4, 4, 4, 5, 5, 6, 7, 7, 8, 8, 8, 9, 9, 9, 10, 10, 10, 10, 10, 10, 10, 10));

            HashMap preds = new HashMap();
            for (int i = 0; i < 40; i++) {
                int tempr = r.nextInt(list.size());
                preds.put(br.readLine(), list.remove(tempr));
            }
            br.close();
            Random rpercent = new Random(11);


            is = new FileInputStream("G://josirefs/hybridsparql/bsbmtools-0.2/dataset.n3");
            NxParser nxp = new NxParser(is);
            while (nxp.hasNext()) {
                Node[] next = nxp.next();
                int j = Integer.parseInt(preds.get(next[1].toN3()).toString());
                if(rpercent.nextInt(11) < j){
                    bw.write(next[0].toN3() + " " + next[1].toN3() + " " + next[2].toN3() + " " + "\"true\" ." + "\n");
                }else{
                    bw.write(next[0].toN3() + " " + next[1].toN3() + " " + next[2].toN3() + " " + "\"false\" ." + "\n");
                }
            }
            bw.flush();
            bw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
