/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btc;

import java.io.FileInputStream;
import java.io.InputStream;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Variable;
import org.semanticweb.yars.nx.parser.NxParser;

/**
 *
 * @author sohdeh
 */
public class statistics {
    private int a;
    public statistics(){}
    
    public double computeF_Value(Node[] triple,String filePath)
    {
        double f_value=0;
        try{
            InputStream is = new FileInputStream(filePath);            
            NxParser nxp = new NxParser(is); 
            double true_val=0,false_value=0;
            while (nxp.hasNext()) {
                Node[] next = nxp.next();
                int i=0;
                for (; i < triple.length ; i++) {
                        if(triple[i] instanceof Variable ) continue;
                        if (!(triple[i].toString().equalsIgnoreCase(next[i].toString())))
                            break;
                    }
                if(i!=triple.length) continue;
                if(next[3].toString().equalsIgnoreCase("true"))true_val++;
                else false_value++;
            }
            f_value=true_val/(false_value+true_val);
        }catch(Exception e){e.printStackTrace();}
        return f_value;
    }    
}
