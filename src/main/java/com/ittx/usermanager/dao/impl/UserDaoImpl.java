package com.ittx.usermanager.dao.impl;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.orm.hibernate4.HibernateCallback;
import org.springframework.stereotype.Repository;

import com.ittx.usermanager.dao.UserDao;
import com.ittx.usermanager.model.User;
import com.ittx.usermanager.util.MyHibernateDaoSupport;

@Repository("userDao")
public class UserDaoImpl extends MyHibernateDaoSupport implements UserDao {
	@Override
	public void addUser(User user) {
		getHibernateTemplate().save(user);
	}

	@Override
	public List<User> getAllUser(String userName, int userSex, int startIndex, int size) {
		StringBuilder sb = new StringBuilder("FROM User WHERE 1=1");
		if(userName != null && !"".equals(userName)){
			sb.append(" AND name like '"+userName+"'");
		}
		if(userSex != -1){
			sb.append(" and sex = "+userSex);
		}
		
		return (List<User>) getHibernateTemplate().find(sb.toString());
	}

	@Override
	public void batchDelete(String userIds) {
		String[] selectFlag = userIds.split(",");
		Integer[] parameters = new Integer[selectFlag.length];
		for(int i = 0; i < selectFlag.length; i++){
			parameters[i] = Integer.parseInt(selectFlag[i]);
		}
		
        final String queryString = "delete User where id in (:ids) ";
        
        getHibernateTemplate().execute(new HibernateCallback<Object>() {
        	@Override
        	public Object doInHibernate(Session session) throws HibernateException {
        		Query query = session.createQuery(queryString);
        		query.setParameterList("ids", parameters);
        		query.executeUpdate();
        		return null;
        	}
		});
	}

	@Override
	public User getUserById(int id) {
		User user = (User)getHibernateTemplate().get(User.class, id);
		return user;
	}

	@Override
	public void updateUser(User user) {
		 getHibernateTemplate().update(user);
	}

	@Override
	public int getTotal(String userName, int userSex) {

		String hql = "SELECT COUNT(*) FROM User WHERE 1=1";
		
		StringBuilder hsb = new StringBuilder(hql);
		if(userName != null && !"".equals(userName)){
			hsb.append(" AND userName like :name");
		}
		if(userSex != -1){
			hsb.append(" AND sex = :sex");
		}
		
		return getHibernateTemplate().execute(new HibernateCallback<Integer>() {
			@Override
			public Integer doInHibernate(Session session) throws HibernateException {
				Query query = session.createQuery(hsb.toString());
				
				if(userName != null && !"".equals(userName)){
					query.setParameter("name", userName);
				}
				if(userSex != -1){
					query.setParameter("sex", userSex);
				}
//				long count = (long) query.uniqueResult();
				int count = ((Number)query.uniqueResult()).intValue();
				return count;
			}
		});
	}
	
	public void getUserByName(){
		List<User> userLists =  (List<User>) getHibernateTemplate().find("FROM User WHERE name = ?", "张三");
		System.out.println("user  :"+userLists.get(0));
	}

}
