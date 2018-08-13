package com.bridgelabz.microserice.fundoonotes.note.repositories;

public interface TokenRepository {

	/**
	 * to save the token as key and userId as value 
	 * @param token
	 * @param userId
	 */
	public void save(String token, String userId);

	/**
	 * to get userId by token
	 * @param token
	 * @return
	 */
	public String get(String token);
	
	/**
	 * to delete token
	 * @param token
	 */
	public void delete(String token);
}
