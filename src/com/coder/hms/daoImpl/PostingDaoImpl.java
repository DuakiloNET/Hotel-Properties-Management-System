package com.coder.hms.daoImpl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.query.Query;

import com.coder.hms.connection.DataSourceFactory;
import com.coder.hms.dao.PostingDAO;
import com.coder.hms.dao.TransactionManagement;
import com.coder.hms.entities.Posting;

public class PostingDaoImpl implements PostingDAO, TransactionManagement {
	
	private Session session;
	private DataSourceFactory dataSourceFactory;

	public PostingDaoImpl() {
		
		dataSourceFactory = new DataSourceFactory();
		DataSourceFactory.createConnection();
	}
	
	@Override
	public void savePosting(Posting posting) {
		session = dataSourceFactory.getSessionFactory().getCurrentSession();
		beginTransactionIfAllowed(session);
		session.saveOrUpdate(posting);
		session.getTransaction().commit();
		session.close();

	}

	@Override
	public boolean deletePosting(long theId) {
		boolean result = false;
		
		try {
			session = dataSourceFactory.getSessionFactory().getCurrentSession();
			beginTransactionIfAllowed(session);
			Query<?> query = session.createQuery("delete Posting where id = :theId");
			query.setParameter("theId", theId);
			query.executeUpdate();
			session.close();
			result = true;
		} catch (HibernateException e) {
			e.printStackTrace();
			result = false;
		}
		return result;
	}

	@Override
	public Posting getPostingById(long Id) {
		session = dataSourceFactory.getSessionFactory().getCurrentSession();
		beginTransactionIfAllowed(session);
		final Posting posting = session.get(Posting.class, Id);
		session.close();
		
		return posting;
	}

	@Override
	public List<Posting> getAllPostingsByRoomNumber(String theRoomNumber) {
		session = dataSourceFactory.getSessionFactory().getCurrentSession();
		beginTransactionIfAllowed(session);
		Query<Posting> query = session.createQuery("from Posting where roomNumber = :theRoomNumber", Posting.class);
		query.setParameter("theRoomNumber", theRoomNumber);
		List<Posting> postList = query.getResultList();
		session.close();
		
		return postList;
	}

	@Override
	public void beginTransactionIfAllowed(Session theSession) {
		if(!theSession.getTransaction().isActive()) {
			theSession.beginTransaction();	
		}else {
			theSession.getTransaction().rollback();
			theSession.beginTransaction();
		}
		
	}

}
