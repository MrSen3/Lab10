package it.polito.tdp.porto.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import it.polito.tdp.porto.model.Author;
import it.polito.tdp.porto.model.Paper;

public class PortoDAO {

	public List <Author> getAllAutori() {
		List<Author> result=new ArrayList<Author>();
		final String sql = "SELECT * FROM author ORDER BY lastname ASC ";

		try {
			
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				result.add(new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname")));
				
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

	
	
	/*
	 * Dato l'id ottengo l'autore.
	 */
	public Author getAutore(int id) {

		final String sql = "SELECT * FROM author where id=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				Author autore = new Author(rs.getInt("id"), rs.getString("lastname"), rs.getString("firstname"));
				return autore;
			}
			conn.close();
			return null;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}

	/*
	 * Dato l'id ottengo l'articolo.
	 */
	public Paper getArticolo(int eprintid) {

		final String sql = "SELECT * FROM paper where eprintid=?";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, eprintid);

			ResultSet rs = st.executeQuery();

			if (rs.next()) {
				Paper paper = new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"),
						rs.getString("publication"), rs.getString("type"), rs.getString("types"));
				return paper;
			}
			conn.close();
			return null;

		} catch (SQLException e) {
			 e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}



	public List<Paper> getAllPubblicazioni() {
		List<Paper> result=new ArrayList<>();
		final String sql = "SELECT * FROM paper";

		try {
			
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				result.add(new Paper(rs.getInt("eprintid"), rs.getString("title"), rs.getString("issn"), rs.getString("publication"), rs.getString("type"), rs.getString("types")));
				
			}
			conn.close();
			return result;

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
	}


	//Mi restituisce la  lista di pubblicazioni di un certo autore

	public List<Paper> getArticoli(Map<Integer, Paper> paperIdMap, Map<Integer, Author> autoriIdMap, int id) {
		// TODO Auto-generated method stub
		List<Paper> result = new ArrayList<>();
		final String sql = "SELECT eprintid " + 
				"FROM creator " + 
				"WHERE authorid=? ";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, id);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				result.add(paperIdMap.get(rs.getInt("eprintid")));
				
			}
			conn.close();

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
		
		return result;	
}


	//Questo metodo mi restituisce la lista di coautori, data una pubblicazione
	//e dato un autore principale il quale viene gia escluso dalla lista restituita dal db
	public List<Author> getCoautori(Map<Integer, Author> autoriIdMap, int eprintid, int id) {
		// TODO Auto-generated method stub
		List<Author> result = new ArrayList<>();
		
		final String sql = "SELECT authorid " + 
				"FROM creator " + 
				"WHERE eprintid=? " + 
				"AND authorid!=? ";

		try {
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, eprintid);
			st.setInt(2, id);

			ResultSet rs = st.executeQuery();

			while (rs.next()) {

				result.add(autoriIdMap.get(rs.getInt("authorid")));
				
			}
			conn.close();

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
		
		return result;
	}



	public Paper getArticoloDatiDueCoAutori(Author partenza, Author arrivo, Map<Integer, Paper> paperIdMap) {
		// TODO Auto-generated method stub
		Paper result=null;
		final String sql ="SELECT eprintid, COUNT(*) " + 
				"FROM creator " + 
				"WHERE  authorid=? || authorid=? " + 
				"GROUP BY eprintid " + 
				"HAVING COUNT(*)>1 "; //metto questa condizione cosi mi restituisce solo la lista di pubblicazioni
		try {
		
			Connection conn = DBConnect.getConnection();
			PreparedStatement st = conn.prepareStatement(sql);
			st.setInt(1, partenza.getId());
			st.setInt(2, arrivo.getId());

			ResultSet rs = st.executeQuery();

			if (rs.next()) {

				result=paperIdMap.get(rs.getInt("eprintid"));
				
			}
			conn.close();

		} catch (SQLException e) {
			// e.printStackTrace();
			throw new RuntimeException("Errore Db");
		}
		
		
		return result;
	}
}