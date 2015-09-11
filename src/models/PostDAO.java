package models;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import models.exceptions.SecurityBreachException;

public class PostDAO extends DAO {

	private final String createQuery = "INSERT INTO `posts` (`id`, `user_id`, `time_posted`, `post`) "
			+ "VALUES (NULL, (SELECT `id` FROM `users` WHERE `username` = ?), NOW(), ?	";
	private final String[] createParams = { "username", "post" };

	private final String editQuery = "UPDATE `posts` SET `datetime_lastedited` = NOW(), `post` = ? WHERE `id` = ?";
	private final String[] editParams = { "post", "id" };

	private final String deleteQuery = "UPDATE `posts` SET `datetime_lastedited` = NOW(), `deleted` = true WHERE `id` = ?";

	private final String getQuery = "SELECT `firstname`, `username`, `datetime_joined`, `datetime_created`, `post`, `datetime_lastedited`, `deleted` "
			+ "FROM `users`, `posts` WHERE `users`.`id` = `posts`.`id` and `posts`.`id` = ?";
	private final String[] getResult = { "firstname", "username", "datetime_joined", "datetime_created", "post",
			"datetime_lastedited", "deleted" };

	private final String getListQuery = "SELECT SQL_CALC_FOUND_ROWS `posts`.`id`, `firstname`, `username`, DATE_FORMAT(`datetime_joined`, '%b %d, %Y') as `datetime_joined`, "
			+ "CONCAT(IF(DATE(`datetime_created`) = DATE(now()), 'Today', IF(DATE(`datetime_created`) = DATE(SUBDATE(now(), 1)), 'Yesterday', DATE_FORMAT(`datetime_created`, '%b %d, %Y'))), ' ', DATE_FORMAT(`datetime_created`,'%h:%i:%s %p')) as `datetime_created`, "
			+ "`post`, CONCAT(IF(DATE(`datetime_lastedited`) = DATE(now()), 'Today', IF(DATE(`datetime_lastedited`) = DATE(SUBDATE(now(), 1)), 'Yesterday', DATE_FORMAT(`datetime_lastedited`, '%b %d, %Y'))), ' ', DATE_FORMAT(`datetime_lastedited`,'%h:%i:%s %p')) as `datetime_lastedited`, `deleted` "
			+ "FROM `users`, `posts` WHERE `users`.`id` = `posts`.`user_id` ORDER BY `posts`.`id` desc LIMIT ?, ?";
	private final String getListRowsQuery = "SELECT FOUND_ROWS()";
	private final String[] getListResult = { "id", "firstname", "username", "datetime_joined", "datetime_created",
			"post", "datetime_lastedited", "deleted" };

	private final String checkQuery = "SELECT EXISTS(SELECT 1 FROM `posts` WHERE `id` = ? and `user_id` = "
			+ "(SELECT `id` FROM `users` WHERE `username` = ?)) as `result`";

	private int noOfRecords;

	public boolean create(Post post) {
		boolean result = false;
		try {
			con = getConnection();
			ps = con.prepareStatement(createQuery);
			for (int i = 0, j = 1, k = createParams.length; i < k; i++, j++)
				ps.setString(j, post.getInfo(createParams[i]));
			ps.executeUpdate();
			result = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public boolean edit(Post post) {
		boolean result = false;
		try {
			con = getConnection();
			ps = con.prepareStatement(editQuery);
			for (int i = 0, j = 1, k = editParams.length; i < k; i++, j++)
				ps.setString(j, post.getInfo(editParams[i]));
			System.out.println(ps);
			ps.executeUpdate();
			result = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public boolean delete(Post post) {
		boolean result = false;
		try {
			con = getConnection();
			ps = con.prepareStatement(deleteQuery);
			ps.setInt(1, post.getId());
			ps.executeUpdate();
			result = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public Post getInfo(int id) {
		Post post = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(getQuery);
			ps.setInt(1, id);
			rs = ps.executeQuery();
			if (rs.next()) {
				post = new Post();
				for (int i = 0, j = 1, k = getResult.length; i < k; i++, j++)
					post.setInfo(getResult[i], rs.getObject(j).toString());
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return post;
	}

	public boolean checkIfCreator(int id, String username) {
		boolean result = false;
		try {
			con = getConnection();
			ps = con.prepareStatement(checkQuery);
			ps.setInt(1, id);
			ps.setString(2, username);
			rs = ps.executeQuery();
			if (rs.next()) if (rs.getInt(1) == 1) result = true;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return result;
	}

	public List<Post> getList(int offset, int recordsPerPage) {
		List<Post> posts = new ArrayList<Post>();
		Post post = null;
		try {
			con = getConnection();
			ps = con.prepareStatement(getListQuery);
			ps.setInt(1, offset);
			ps.setInt(2, recordsPerPage);
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				post = new Post();
				for (int i = 0, j = 1, k = getListResult.length; i < k; i++, j++)
					if (rs.getObject(j) != null) post.setInfo(getListResult[i], rs.getObject(j).toString());
				posts.add(post);
			}
			ps = con.prepareStatement(getListRowsQuery);
			rs = ps.executeQuery();
			if (rs.next()) noOfRecords = rs.getInt(1);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				if (ps != null) ps.close();
				if (rs != null) rs.close();
				if (con != null) con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return posts;
	}

	public int getNoOfRecords() {
		return noOfRecords;
	}

}
