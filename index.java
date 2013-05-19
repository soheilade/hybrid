/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btc;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import org.apache.jena.riot.RDFDataMgr;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.Variable;
import org.semanticweb.yars.nx.parser.NxParser;

/**
 *
 * @author sohdeh
 */
public class statistics {
    public statistics(){}
     public static void main(String[] args) {
         statistics s=new statistics();
         s.computeF_Value("G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq");
    }
    public void computeF_Value(String filePath)
    {
        //creat index for S ?p ?o,S p ?o,? p ?,? p o, s ? o,? ? O.
        HashMap tps=new HashMap();
        HashMap tpsp=new HashMap();
        HashMap tpp=new HashMap();
        HashMap tppo=new HashMap();
        HashMap tpso=new HashMap();
        HashMap tpo=new HashMap();
        
        double f_value=0;
        try{
            HashMap shm= new HashMap();
            HashMap phm= new HashMap();
            HashMap ohm= new HashMap();
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File("G:/josirefs/hybridsparql/querylog/index.dat")));        
            InputStream is = new FileInputStream(filePath);            
            NxParser nxp = new NxParser(is); 
            double true_val=0,false_value=0;
            NodeArrayWrapper temp=null;
            //int j=0;
            Node[] tp=null;
            while (nxp.hasNext()) {                
                Node[] next = nxp.next();
                //S ?p ?o index on tps
                tp=new Node[3]; tp[0]=new Resource(next[0].toString());tp[1]=new Variable("pred"); tp[2]=new Variable("obj"); 
                NodeArrayWrapper tpw=new NodeArrayWrapper(tp);
                //if (j==1) {boolean x= tpw.equals(temp);}
                Object fvalue = tps.get(tpw);
                if (fvalue!=null)
                {
                    String[] str =fvalue.toString().split(",");
                    int total= Integer.parseInt(str[1])+1;
                    int f=Integer.parseInt(str[0]);
                    if(next[3].toString().equalsIgnoreCase("true")) f++;
                    tps.put(tpw, Integer.toString(f)+","+Integer.toString(total));
                }else {
                    if(next[3].toString().equalsIgnoreCase("true")) tps.put(tpw, "1,1");
                    else tps.put(tpw, "0,1");
                }                
                //S p ?o tpsp
                tp=new Node[3]; tp[0]=new Resource(next[0].toString());tp[1]=new Resource(next[1].toString()); tp[2]=new Variable("obj"); 
                tpw=new NodeArrayWrapper(tp);
                //if (j==1) {boolean x= tpw.equals(temp);}
                fvalue = tpsp.get(tpw);
                if (fvalue!=null)
                {
                    String[] str =fvalue.toString().split(",");
                    int total= Integer.parseInt(str[1])+1;
                    int f=Integer.parseInt(str[0]);
                    if(next[3].toString().equalsIgnoreCase("true")) f++;
                    tpsp.put(tpw, Integer.toString(f)+","+Integer.toString(total));
                }else {
                    if(next[3].toString().equalsIgnoreCase("true")) tpsp.put(tpw, "1,1");
                    else tpsp.put(tpw, "0,1");
                }
                             
                //? p ?
                tp=new Node[3]; tp[0]=new Resource(next[0].toString());tp[1]=new Resource(next[1].toString()); tp[2]=new Variable("obj"); 
                tpw=new NodeArrayWrapper(tp);
                //if (j==1) {boolean x= tpw.equals(temp);}
                fvalue = tpp.get(tpw);
                if (fvalue!=null)
                {
                    String[] str =fvalue.toString().split(",");
                    int total= Integer.parseInt(str[1])+1;
                    int f=Integer.parseInt(str[0]);
                    if(next[3].toString().equalsIgnoreCase("true")) f++;
                    tpp.put(tpw, Integer.toString(f)+","+Integer.toString(total));
                }else {
                    if(next[3].toString().equalsIgnoreCase("true")) tpp.put(tpw, "1,1");
                    else tpp.put(tpw, "0,1");
                }
                             
                //? p o
                tp=new Node[3]; tp[0]=new Resource(next[0].toString());tp[1]=new Resource(next[1].toString()); tp[2]=new Variable("obj"); 
                tpw=new NodeArrayWrapper(tp);
                //if (j==1) {boolean x= tpw.equals(temp);}
                fvalue = tppo.get(tpw);
                if (fvalue!=null)
                {
                    String[] str =fvalue.toString().split(",");
                    int total= Integer.parseInt(str[1])+1;
                    int f=Integer.parseInt(str[0]);
                    if(next[3].toString().equalsIgnoreCase("true")) f++;
                    tppo.put(tpw, Integer.toString(f)+","+Integer.toString(total));
                }else {
                    if(next[3].toString().equalsIgnoreCase("true")) tppo.put(tpw, "1,1");
                    else tppo.put(tpw, "0,1");
                }
                              
                //s ? o
                tp=new Node[3]; tp[0]=new Resource(next[0].toString());tp[1]=new Resource(next[1].toString()); tp[2]=new Variable("obj"); 
                tpw=new NodeArrayWrapper(tp);
                //if (j==1) {boolean x= tpw.equals(temp);}
                fvalue = tpso.get(tpw);
                if (fvalue!=null)
                {
                    String[] str =fvalue.toString().split(",");
                    int total= Integer.parseInt(str[1])+1;
                    int f=Integer.parseInt(str[0]);
                    if(next[3].toString().equalsIgnoreCase("true")) f++;
                    tpso.put(tpw, Integer.toString(f)+","+Integer.toString(total));
                }else {
                    if(next[3].toString().equalsIgnoreCase("true")) tpso.put(tpw, "1,1");
                    else tpso.put(tpw, "0,1");
                }
                             
                //? ? O
                tp=new Node[3]; tp[0]=new Resource(next[0].toString());tp[1]=new Resource(next[1].toString()); tp[2]=new Variable("obj"); 
                tpw=new NodeArrayWrapper(tp);
                //if (j==1) {boolean x= tpw.equals(temp);}
                fvalue = tpo.get(tpw);
                if (fvalue!=null)
                {
                    String[] str =fvalue.toString().split(",");
                    int total= Integer.parseInt(str[1])+1;
                    int f=Integer.parseInt(str[0]);
                    if(next[3].toString().equalsIgnoreCase("true")) f++;
                    tpo.put(tpw, Integer.toString(f)+","+Integer.toString(total));
                }else {
                    if(next[3].toString().equalsIgnoreCase("true")) tpo.put(tpw, "1,1");
                    else tpo.put(tpw, "0,1");
                }
               
                //temp=new NodeArrayWrapper(tpw.getdata()); j++;
            }
            bw.write("=====================tps\n");                
            //writing down the tps index to file
                Iterator it =tps.keySet().iterator();
                while (it.hasNext()){
                    NodeArrayWrapper n=(NodeArrayWrapper) it.next();
                    String f = tps.get(n).toString();
                    Node[] narr=n.getdata();
                    bw.write(narr[0].toN3()+" "+narr[1].toN3()+" "+narr[2].toN3()+" "+ f + "\n");
                }
            bw.write("=====================tpsp\n");
            it = tpsp.keySet().iterator();
            while (it.hasNext()) {
                NodeArrayWrapper n = (NodeArrayWrapper) it.next();
                String f = tpsp.get(n).toString();
                Node[] narr = n.getdata();
                bw.write(narr[0].toN3() + " " + narr[1].toN3() + " " + narr[2].toN3() + " " + f + "\n");
            }
            bw.write("=====================tpp\n");
            it = tpp.keySet().iterator();
            while (it.hasNext()) {
                NodeArrayWrapper n = (NodeArrayWrapper) it.next();
                String f = tpp.get(n).toString();
                Node[] narr = n.getdata();
                bw.write(narr[0].toN3() + " " + narr[1].toN3() + " " + narr[2].toN3() + " " + f + "\n");
            }
            bw.write("=====================tppo\n");
            it = tppo.keySet().iterator();
            while (it.hasNext()) {
                NodeArrayWrapper n = (NodeArrayWrapper) it.next();
                String f = tppo.get(n).toString();
                Node[] narr = n.getdata();
                bw.write(narr[0].toN3() + " " + narr[1].toN3() + " " + narr[2].toN3() + " " + f + "\n");
            }
            bw.write("=====================tpso\n");
            it = tpso.keySet().iterator();
            while (it.hasNext()) {
                NodeArrayWrapper n = (NodeArrayWrapper) it.next();
                String f = tpso.get(n).toString();
                Node[] narr = n.getdata();
                bw.write(narr[0].toN3() + " " + narr[1].toN3() + " " + narr[2].toN3() + " " + f + "\n");
            }
            bw.write("=====================tpo\n");
            it = tpo.keySet().iterator();
            while (it.hasNext()) {
                NodeArrayWrapper n = (NodeArrayWrapper) it.next();
                String f = tpo.get(n).toString();
                Node[] narr = n.getdata();
                bw.write(narr[0].toN3() + " " + narr[1].toN3() + " " + narr[2].toN3() + " " + f + "\n");
            }
            bw.flush();
            bw.close();
        }catch(Exception e){e.printStackTrace();}
        
    }
    public final class NodeArrayWrapper
{
    private final Node[] data;
    public Node[] getdata(){return data;}

    public NodeArrayWrapper(Node[] data)
    {
        if (data == null)
        {
            throw new NullPointerException();
        }
        this.data = data;
    }

    @Override
    public boolean equals(Object other)
    {
        if ( other == null ) return false;
        if ( this.getClass() != other.getClass() ) return false;
        Node[] a=((NodeArrayWrapper)other).data;
        for(int i=0;i<3;i++)
        {
            //if(data[i] instanceof Variable && a[i] instanceof Variable) return false;//otherwise it will match all S ?p ?o with S p ?o and won't consider second one at all
            if(data[i] instanceof Variable ) continue;
            //we should ignore this case because when we compare triple pattern with triple (in comparing data from dataset to find those who matches a pattern) 
            //we ignore variables but when we compare triple pattern with triple pattern in hashmaps we shouldn't ignore variables of the parameter
            if(a[i] instanceof Variable ) continue;//this won't happen when comparing datafile to find matches but will happen when comparing 2 triple pattern
            if(!data[i].toString().equalsIgnoreCase(a[i].toString())) return false;
        }        
        return true;
    }
    @Override
    public int hashCode()
    {
        return Arrays.hashCode(data);
    }

}   
}
