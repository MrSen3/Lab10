package it.polito.tdp.porto.model;

import java.util.*;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.Graphs;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

import it.polito.tdp.porto.db.PortoDAO;

public class Model {
	
	private Graph<Author, DefaultEdge> grafo;
	private List<Author> autori;
	private List<Paper> pubblicazioni;
	private Map<Integer, Author> autoriIdMap;
	private Map<Integer, Paper> paperIdMap;
	private PortoDAO dao;


	public Model() {
		
		grafo=new SimpleGraph<Author, DefaultEdge>(DefaultEdge.class);
		autori=new ArrayList<Author>();
		autoriIdMap=new HashMap<>();
		paperIdMap=new HashMap<>();
		
		dao=new PortoDAO();
		this.autori=dao.getAllAutori();
		this.pubblicazioni=dao.getAllPubblicazioni();
	}
	

	public List<Author> getAutori() {
		return this.autori;
	}
	
	public List<Paper> getPubblicazioni() {
		return this.pubblicazioni;
	}
	


	public void creaGrafo() {
		// TODO Auto-generated method stub
		
		for(Author a : this.autori) {
			autoriIdMap.put(a.getId(), a);
		}
		//Aggiungo i vertici
		Graphs.addAllVertices(this.grafo, this.autori);
		
		System.out.println("Creato un grafo di "+this.grafo.vertexSet().size()+" vertici ");
		
		
		for(Paper p : this.pubblicazioni) {
			paperIdMap.put(p.getEprintid(), p);
		}
		//Ora devo aggiungere gli archi
		//Un arco esiste se due autori hanno collaborato almeno una volta
		//L'unica tabella da cui posso estrarre le collaborazioni e'

		//per ogni autore
		for(Author a: grafo.vertexSet()) {
			//trovo le sue pubblicazioni---->mi serve un metodo nel dao che dato  un autore mi restituisca tutte le sue pubblicazioni
			List<Paper> pubblicazioniAutore = new ArrayList<>(dao.getArticoli(paperIdMap, autoriIdMap,  a.getId()));
			//per ogni pubblicazione trovo i coautori
			for(Paper p: pubblicazioniAutore) {
				List<Author>coautori=new ArrayList<>(dao.getCoautori(autoriIdMap, p.getEprintid(), a.getId()));
				if(coautori!=null) {
					
					//se ce ne sono, per ogni coautore aggiungo un arco
					for(Author c:coautori) {
						Graphs.addEdgeWithVertices(grafo, a, c);
					}
				}
			}
		
		}
		
		System.out.println("e "+this.grafo.edgeSet().size()+" archi ");
		
	}


	public List<Author> trovaCoAutori(int id) {
		// TODO Auto-generated method stub
		List<Author> coautori = new ArrayList<Author>(Graphs.neighborListOf(grafo, autoriIdMap.get(id)));
		return coautori;
	}
	
	public List<Author> trovaNonCoAutori(List<Author>coautori, Author autoreScelto){
		List<Author> nonCoAutori=new ArrayList<>();
		for(Author a1: autori) {
			if(!coautori.contains(a1) && a1!=autoreScelto) {
				nonCoAutori.add(a1);
			}
		}
		return nonCoAutori;
	}


	public List<Paper> trovaSequenza(Author partenza, Author arrivo) {
		// TODO Auto-generated method stub
		List<Paper> result = new LinkedList<>();
		DijkstraShortestPath<Author, DefaultEdge> dijkstra = new DijkstraShortestPath<Author, DefaultEdge>(grafo);
		GraphPath<Author, DefaultEdge> path = dijkstra.getPath(partenza, arrivo);
		
		
		if(path==null) {
			System.out.println("Non esiste un collegamento tra i due autori\n");
		}
		List<Author>percorso=path.getVertexList();
		
		System.out.println("Trovato un percorso minimo lungo "+path.getVertexList().size()+" tappe.\n");
		//Per ogni vertice del percorso cerco un articolo in cui hanno collaborato, ma sapendo gia che sicuramente esiste
		
		for(int i=1; i<path.getVertexList().size(); i++) {
			Author pre=percorso.get(i-1);
			Author post=percorso.get(i);
			Paper step=dao.getArticoloDatiDueCoAutori(pre, post, paperIdMap);
			result.add(step);
			
			System.out.println(step.toString()+"\n");
		}
		//Salvo tutti gli articoli trovati in result e dopodiche lo ripasso al controller che stampera tutto
		
		return result;
	}


	public Map<Integer, Author> getAutoriIdMap() {
		return autoriIdMap;
	}


	public Map<Integer, Paper> getPaperIdMap() {
		return paperIdMap;
	}

	

}
