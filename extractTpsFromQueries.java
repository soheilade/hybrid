/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package btc;

import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.core.PathBlock;
import com.hp.hpl.jena.sparql.core.TriplePath;
import com.hp.hpl.jena.sparql.syntax.Element;
import com.hp.hpl.jena.sparql.syntax.ElementGroup;
import com.hp.hpl.jena.sparql.syntax.ElementPathBlock;
import com.hp.hpl.jena.sparql.syntax.ElementSubQuery;
import com.hp.hpl.jena.sparql.syntax.ElementTriplesBlock;
import com.hp.hpl.jena.util.FileManager;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.Resource;
import org.semanticweb.yars.nx.Variable;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.reorder.ReorderIterator;

/**
 *
 * @author sohdeh
 */
public class extracttriplepatternsusingJena {
    public static void main(String[] args) {
        Comparator<Node[]> var = new Comparator<Node[]>() {
                public int compare(Node[] o1, Node[] o2) {
                    int diff = 0;
                    //just need to compare predicate=2 and object=3 because we are matching subject
                    for (int i = 1; i < o1.length - 1; i++) {
                        diff = o1[i].toN3().trim().compareTo(o2[i].toN3().trim());
                        if (diff != 0) {
                            return diff;
                        }
                    }
                    return diff;
                }
            };
        try {
            Map<Node[], Double> tpS = new HashMap<Node[], Double>();
            Map<Node[], Double> tpP = new HashMap<Node[], Double>();
            Map<Node[], Double> tpO = new HashMap<Node[], Double>();
            statistics s = new statistics();
            String nxpath = "G://josirefs/hybridsparql/bsbmtools-0.2/datasetvalidp2.nq";
            
            //extract new triplr patterns from queries
             BufferedReader br=new BufferedReader(new FileReader(new File("G:/josirefs/hybridsparql/querylog/querylog.n3")));
            String str="";
            String tempQuery="";
            ArrayList QArray=new ArrayList();
            ArrayList tpArray=new ArrayList();
            HashMap tphm=new HashMap();
            while((str=br.readLine())!=null){
                if(str.startsWith(">>")){//new query
                    QArray.add(tempQuery);
                    tempQuery="";
                }
                else {
                    tempQuery=tempQuery.concat("\n"+str);
                }
            }
            for(int i=0;i<QArray.size();i++){
                Query q1= QueryFactory.create(QArray.get(i).toString());
                ElementGroup eg = (ElementGroup)q1.getQueryPattern();
                List<Element> l= eg.getElements();
                Iterator it=l.iterator();
                while(it.hasNext()){//extracting triple patterns from queries
                    Object otriple=it.next();
                    if(otriple instanceof ElementPathBlock)
                    {
                        ElementPathBlock e=(ElementPathBlock)otriple;                        
                     PathBlock strs=e.getPattern();
                     for(int k=0;k<strs.size();k++)//foreach triple pattern
                     {
                         Node [] pattern1=new Node[4];
                         //Node st,pt,ot;
                         int flags=0,flagp=0,flago=0;
                         TriplePath elem= strs.get(k);
                         if(elem.getSubject().isVariable()){
                             pattern1[0]=new Variable(elem.getSubject().toString());
                             flags=1;
                         }else pattern1[0]=new Resource(elem.getSubject().toString()); 
                         if(elem.getPredicate().isVariable()){
                             pattern1[1]=new Variable(elem.getPredicate().toString());
                             flagp=1;
                         }else pattern1[1]=new Resource(elem.getPredicate().toString());
                         if(elem.getObject().isVariable()){
                             pattern1[2]=new Variable(elem.getObject().toString());
                             flago=1;
                         }else pattern1[2]=new Resource(elem.getObject().toString());
                         
                         //tpArray.add(pattern1);
                         tphm.put(pattern1[0].toN3()+" "+pattern1[1].toN3()+" "+pattern1[2].toN3()+" .\n",1);
                     }
                }
            }
            }            
            BufferedWriter bw=new BufferedWriter(new FileWriter(new File("G:/josirefs/hybridsparql/querylog/uniquequeryTPs.n3")));
            Iterator tpit=tphm.keySet().iterator();
            while(tpit.hasNext())
            {
                //Node[] temp = (Node[])tpit.next();
                //bw.write(temp[0].toN3()+" "+temp[1].toN3()+" "+temp[2].toN3()+" .\n");
                bw.write(tpit.next().toString());
            }
            /*for(int kk=0;kk<tpArray.size();kk++)
            {
                Node[] temp=new Node[4];
                temp=(Node[])tpArray.get(kk);
                bw.write(temp[0].toN3()+" "+temp[1].toN3()+" "+temp[2].toN3()+" .\n");
            }
            
            */
            bw.flush();
            bw.close();            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
