package it.polito.tdp.extflightdelays.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleDirectedGraph;

import it.polito.tdp.extflightdelays.db.ExtFlightDelaysDAO;

public class Model {
	
	private List<Airport> aeroporti;
	Map<Integer, Airport> aeroportiIdMap;
	private Graph<Airport, DefaultEdge> grafo;
	
	public List<Airport> getAeroporti(){
		if(this.aeroporti == null) {
			ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
			this.aeroporti = dao.loadAllAirports();
			
			this.aeroportiIdMap = new HashMap<Integer, Airport>();
			for(Airport a : this.aeroporti)
				aeroportiIdMap.put(a.getId(), a);
		}
		return this.aeroporti;
	}
	
	public void creaGrafo() {
		this.grafo = new SimpleDirectedGraph<Airport, DefaultEdge>(DefaultEdge.class);
		
//		Graphs.addAllVertices(this.grafo, this.aeroporti);
		Graphs.addAllVertices(this.grafo, getAeroporti());
		
		ExtFlightDelaysDAO dao = new ExtFlightDelaysDAO();
		
		System.out.println(this.grafo);
		
	}

}
