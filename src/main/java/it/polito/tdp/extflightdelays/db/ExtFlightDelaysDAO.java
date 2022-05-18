package it.polito.tdp.extflightdelays.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


import it.polito.tdp.extflightdelays.model.Airline;
import it.polito.tdp.extflightdelays.model.Airport;
import it.polito.tdp.extflightdelays.model.Flight;
import it.polito.tdp.extflightdelays.model.CoppiaId;

public class ExtFlightDelaysDAO {

	public List<Airline> loadAllAirlines() {
		String sql = "SELECT * from airlines";
		List<Airline> result = new ArrayList<Airline>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				result.add(new Airline(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRLINE")));
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Airport> loadAllAirports() {
		String sql = "SELECT * FROM airports";
		List<Airport> result = new ArrayList<Airport>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Airport airport = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
						rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"), rs.getDouble("LATITUDE"),
						rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				result.add(airport);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}

	public List<Flight> loadAllFlights() {
		String sql = "SELECT * FROM flights";
		List<Flight> result = new LinkedList<Flight>();

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();

			while (rs.next()) {
				Flight flight = new Flight(rs.getInt("ID"), rs.getInt("AIRLINE_ID"), rs.getInt("FLIGHT_NUMBER"),
						rs.getString("TAIL_NUMBER"), rs.getInt("ORIGIN_AIRPORT_ID"),
						rs.getInt("DESTINATION_AIRPORT_ID"),
						rs.getTimestamp("SCHEDULED_DEPARTURE_DATE").toLocalDateTime(), rs.getDouble("DEPARTURE_DELAY"),
						rs.getDouble("ELAPSED_TIME"), rs.getInt("DISTANCE"),
						rs.getTimestamp("ARRIVAL_DATE").toLocalDateTime(), rs.getDouble("ARRIVAL_DELAY"));
				result.add(flight);
			}

			conn.close();
			return result;

		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}	
	}
	
	public boolean isAirportConnected(Airport partenza, Airport arrivo) {
		
		final String sql = "SELECT COUNT(*) AS cnt "
				+ "FROM flights "
				+ "WHERE ORIGIN_AIRPORT_ID = ? "
				+ "AND DESTINATION_AIRPORT_ID = ? ";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getId());
			st.setInt(2, arrivo.getId());
			ResultSet rs = st.executeQuery();
			
			rs.first();
			int count = rs.getInt("cnt");
			st.close();
			conn.close();
			
			return count>0;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
	}
	
	public List<Integer> getIfAirportConected(Airport partenza){
		
		final String sql = "SELECT DESTINATION_AIRPORT_ID "
				+ "FROM flights  "
				+ "WHERE ORIGIN_AIRPORT_ID = ? "
				+ "GROUP BY DESTINATION_AIRPORT_ID ";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getId());
			ResultSet rs = st.executeQuery();
			
			List<Integer> idAirport = new ArrayList<Integer>();
			
			while (rs.next()) {
				idAirport.add(rs.getInt("DESTINATION_AIRPORT_ID"));
			}
			
			st.close();
			conn.close();
			
			return idAirport;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<Airport> getAirportConnected(Airport partenza){
		
		final String sql = "SELECT ID, IATA_CODE, AIRPORT, CITY, STATE, COUNTRY, LATITUDE, LONGITUDE, TIMEZONE_OFFSET "
				+ "FROM airports "
				+ "WHERE ID IN ( "
				+ "SELECT DESTINATION_AIRPORT_ID "
				+ "FROM flights "
				+ "WHERE ORIGIN_AIRPORT_ID = ? "
				+ "GROUP BY DESTINATION_AIRPORT_ID "
				+ ") "
				+ "ORDER BY AIRPORT ASC ";
		
		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getId());
			ResultSet rs = st.executeQuery();
			
			List<Airport> aeroporti = new ArrayList<Airport>();
			
			while (rs.next()) {
				Airport a = new Airport(rs.getInt("ID"), rs.getString("IATA_CODE"), rs.getString("AIRPORT"),
											rs.getString("CITY"), rs.getString("STATE"), rs.getString("COUNTRY"),
												rs.getDouble("LATITUDE"), rs.getDouble("LONGITUDE"), rs.getDouble("TIMEZONE_OFFSET"));
				aeroporti.add(a);
			}
			
			st.close();
			conn.close();
			
			return aeroporti;
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Errore connessione al database");
			throw new RuntimeException("Error Connection Database");
		}
		
	}
	
	public List<CoppiaId> getAllAirportConnected(){
		
		final String sql = "SELECT DISTINCT ORIGIN_AIRPORT_ID, DESTINATION_AIRPORT_ID FROM flights";
		

		try {
			Connection conn = ConnectDB.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			ResultSet rs = st.executeQuery();
			
			List<CoppiaId> result = new ArrayList<CoppiaId>();
			
			while (rs.next()) {
				CoppiaId c = new CoppiaId(rs.getInt("id_stazP"), rs.getInt("id_stazA"));
				result.add(c);
			}

			st.close();
			conn.close();
			
			return result;
			
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Errore di connessione al Database.");
		}

		
		
	}
	
	
}
