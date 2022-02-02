package hw2;
/**
 * The blueprint for a normal match of tennis, with serving, faulting, returning, and deuces.
 * @author Alex Huynh
 */
public class TennisMatch {
	
	/**
	 * Amount of points needed over a deuce to have an advantage in a game
	 */
	private static final int ADVANTAGE_GAME = 1;
	
	/**
	 * Amount of set points needed over the opponent to win a set
	 */
	private static final int ADVANTAGE_SET = 2;
	
	/**
	 * Amount of points needed over opponent during a deuce to win a game
	 */
	private static final int TO_WIN_DEUCE = 2;
	
	/**
	 * The first tennis player
	 */
	private TennisPlayer p1;
	
	/**
	 * The second tennis player
	 */
	private TennisPlayer p2;
	
	/**
	 * Whether the match is played on a best of three (true) or best of five (false)
	 */
	private boolean playBestOfThree;
	
	/**
	 * Whether the game has finished (true) or is still being played (false)
	 */
	private boolean gameOver;
	
	/**
	 * The name of the player who is currently serving
	 */
	private String server;
	
	/**
	 * Whether the ball has been just served (true) or if it has been returned / out of play (false)
	 */
	private boolean serveStatus;
	
	/**
	 * The name of the player who last hit the ball successfully
	 */
	private String lastGoodHit;
	
	/**
	 * Whether the serve was illegal (true)
	 */
	private boolean firstFault;
	
	/**
	 * Whether the ball is currently in motion (true)
	 */
	private boolean ballInPlay;
	
	/**
	 * Whether the score is tied at 40-40 (true)
	 */
	private boolean deuce;

	/**
	 * Constructs a new tennis match
	 * @param p1Name
	 *  the name of the first player
	 * @param p2Name
	 *  the name of the second player
	 * @param playBestOfThree
	 *  whether the match is a best of three (true) or best of five (false)
	 * @param initialServer
	 *  the name of the player who serves first
	 * @param initialServerEnd
	 *  the side (1 or 2) that the server starts on
	 */
	public TennisMatch(String p1Name, String p2Name, boolean playBestOfThree, int initialServer, int initialServerEnd)
	{
		this.playBestOfThree = playBestOfThree;
		
		if(initialServer == 1)
		{
			p1 = new TennisPlayer(p1Name, initialServerEnd);
			server = p1.getName();
			if(initialServerEnd == 1)
			{
				p2 = new TennisPlayer(p2Name, 2);
			}
			else
			{
				p2 = new TennisPlayer(p2Name, 1);
			}
		}
		
		if(initialServer == 2)
		{
			p2 = new TennisPlayer(p2Name, initialServerEnd);
			server = p2.getName();
			if(initialServerEnd == 1)
			{
				p1 = new TennisPlayer(p1Name, 2);
			}
			else
			{
				p1 = new TennisPlayer(p1Name, 1);
			}
		}
		
		firstFault = false;
		serveStatus = false;
		lastGoodHit = "";
		ballInPlay = false;
		deuce = false;
		gameOver = false;
	}
	
	/**
	 * Swaps the server and receiver
	 */
	public void changeServer()
	{
		if(server == p1.getName())
		{
			server = p2.getName();
		}
		else
		{
			server = p1.getName();
		}
	}
	
	/**
	 * Swaps the ends of the two players
	 */
	public void changeEnds()
	{
		if(p1.getEnd() == 1)
		{
			p1.setEnd(2);
			p2.setEnd(1);
		}
		else
		{
			p1.setEnd(1);
			p2.setEnd(2);
		}
	}
	
	/**
	 * Serves the ball. Does nothing if the game is over.
	 */
	public void serve()
	{
		if(!gameOver)
		{
			serveStatus = true;
			lastGoodHit = server;
			ballInPlay = true;
		}
	}
	
	/**
	 * Registers a serve fault. Does nothing if the ball is not being served. 
	 * Two serve faults yield a game point for the receiver.
	 */
	public void fault()
	{
		ballInPlay = false;

		if(serveStatus)
		{
			if(firstFault == true)
			{
				if(server == p1.getName())
				{
					incrementGamePoints(p2, p1);
				}
				else
				{
					incrementGamePoints(p1, p2);
				}
				firstFault = false;
			}
			else
			{
				firstFault = true;
			}
		}
		serveStatus = false;
	}
	
	/**
	 * Reverses the direction of the ball. Ball is no longer being served. Does nothing if the ball is not in play.
	 */
	public void returnBall()
	{
		serveStatus = false;
		
		if(ballInPlay)
		{
			if(lastGoodHit == p1.getName())
			{
				lastGoodHit = p2.getName();
			}
			else
			{
				lastGoodHit = p1.getName();
			}
		}
	}
	
	/**
	 * Takes the ball out of play. The player who last served or returned the ball scores a game point.
	 */
	public void failedReturn()
	{
		serveStatus = false;
		ballInPlay = false;
		
		if(lastGoodHit == p1.getName())
		{
			incrementGamePoints(p1, p2);
		}
		else
		{
			incrementGamePoints(p2, p1);
		}
	}
		
	/**
	 * Returns the name of the player who last successfully served or returned the ball or "Ball not in play"
	 * @return
	 * 	ballFrom's name
	 */
	public String getBallFrom()
	{
		if(serveStatus == true)
		{
			return server;
		}
		else if(!ballInPlay)
		{	
			return "Ball not in play";
		}
		else 
		{
			return lastGoodHit;
		}
	}
	
	/**
	 * Returns the name of the player whom the ball is heading toward or "Ball not in play"
	 * @return
	 * 	ballTo's name
	 */
	public String getBallTo()
	{
		if(ballInPlay)
		{
			if(server == p1.getName() && serveStatus == true)
			{
				return p2.getName();
			}
			else if(server == p2.getName() && serveStatus == true)
			{
				return p1.getName();
			}
			else if(lastGoodHit == p1.getName())
			{
				return p2.getName();
			}
			else if(lastGoodHit == p2.getName())
			{
				return p1.getName();
			}
		}
		return "Ball not in play";
	}
	
	/**
	 * Returns ballInPlay
	 * @return
	 * 	ballInPlay
	 */
	public boolean getBallInPlay()
	{
		return ballInPlay;
	}
	
	/**
	 * Returns ballServed
	 * @return
	 * 	ballServed
	 */	
	public boolean getBallServed()
	{
		return serveStatus;
	}

	/**
	 * returns bestOfThree
	 * @return
	 * 	bestOfThree
	 */
	public boolean getBestOfThree()
	{
		return playBestOfThree;
	}
	
	/**
	 * Returns gameOver
	 * @return
	 * 	gameOver
	 */
	public boolean getGameOver()
	{
		return gameOver;
	}
	
	/**
	 * Returns the game score p1-p1, Advantage name or Deuce. See section 5 of the Friend of the Court.
	 * @return
	 * The game score
	 */
	public String getGameScore()
	{
		
		String scoreP1 = "";
		String scoreP2 = "";
		
		if(p1.getGamePoints() == 0)
		{
			scoreP1 = "Love";
		} 
		else if(p1.getGamePoints() == 1)
		{
			scoreP1 = "15";
		} 
		else if(p1.getGamePoints() == 2)
		{
			scoreP1 = "30";
		} 
		else if(p1.getGamePoints() == 3)
		{
			scoreP1 = "40";	
		}
		else if( (p1.getGamePoints() == p2.getGamePoints() + ADVANTAGE_GAME) && p1.getGamePoints() > 3)
		{
			return "Advantage " + p1.getName();
		}
		
		if(p2.getGamePoints() == 0)
		{
			scoreP2 = "Love";
		} 
		else if(p2.getGamePoints() == 1)
		{
			scoreP2 = "15";
		} 
		else if(p2.getGamePoints() == 2)
		{
			scoreP2 = "30";
		} 
		else if(p2.getGamePoints() == 3)
		{
			scoreP2 = "40";
		} 
		else if( (p2.getGamePoints() == p1.getGamePoints() + ADVANTAGE_GAME) && p2.getGamePoints() > 3)
		{
			return "Advantage " + p2.getName();
		}
		
		if(p1.getGamePoints() == p2.getGamePoints() && p1.getGamePoints() >= 3)
		{
				return "Game score: Deuce";
		} 
		
		return "Game score: " + scoreP1 + "-" + scoreP2;
	}

	/**
	 * Returns the match score p1-p2
	 * @return
	 * 	The match score
	 */
	public String getMatchScore()
	{
		return "Match score: " + p1.getMatchPoints() + "-" + p2.getMatchPoints();
	}
	
	/**
	 * Returns the set score p1-p2
	 * @return
	 *	The set score
	 */
	public String getSetScore()
	{
		return "Set score: " + p1.getSetPoints() + "-" + p2.getSetPoints();
	}
	
	/**
	 * Returns the full game, set, and match score
	 * @return
	 * 	the score
	 */
	public String getScore()
	{
		return getGameScore() + "\n" + getSetScore() + "\n" + getMatchScore();
	}
	
	/**
	 * Return player's name
	 * @param player
	 * 	the player
	 * @return
	 * 	player's name
	 */
	public String getName(int player)
	{
		if(player == 1)
		{
			return p1.getName();
		}
		return p2.getName();

	}
	
	/**
	 * Returns p1's end
	 * @return
	 * 	p1's end
	 */
	public int getP1End()
	{
		return p1.getEnd();
	}
	
	/**
	 * Returns p2's end
	 * @return
	 * 	p2's end
	 */
	public int getP2End()
	{
		return p2.getEnd();
	}
	
	/**
	 * Returns the server's name or "No server"
	 * @return
	 * 	the server's name
	 */
	public String getServer()
	{
		if(!gameOver)
		{
			return server;
		}
		return "No server";
	}
	
	/**
	 * Returns the reveiver's name or "No receiver"
	 * @return
	 * 	the receiver's name
	 */
	public String getReceiver()
	{
		if(!gameOver)
			if(server == p1.getName())
			{
				return p2.getName();
			}
			if(server == p2.getName())
			{
				return p1.getName();
			}
		return "No receiver";
	}
	
	/**
	 * Returns serve fault status
	 * @return
	 * 	serve fault status
	 */
	public boolean getServeFault()
	{
		return firstFault;
	}
	
	/**
	 * Returns the winner's name, or an error message if the game is not over.
	 * @return
	 * 	the winner
	 */
	public String getWinner()
	{
		if(playBestOfThree)
		{
			if(p1.getMatchPoints() == 2)
			{
				return p1.getName();
			}
			else if(p2.getMatchPoints() == 2)
			{
				return p2.getName();
			}
			else
			{
				return "Game is not over";
			}
		}
		else
		{
			if(p1.getMatchPoints() == 3)
			{
				return p1.getName();
			}
			else if(p2.getMatchPoints() == 3)
			{
				return p2.getName();
			}
			else
			{
				return "Match is not over";
			}
		}
	}
	
	/**
	 * Adds one game point to addTo's game total. Zeros game score and increments set score if game has ended. 
	 * Removes ball from play. Clears faults.
	 * @param addTo
	 * 	The player who has scored a point
	 * @param noAdd
	 * 	The other player
	 */
	public void incrementGamePoints(TennisPlayer addTo, TennisPlayer noAdd)
	{
		ballInPlay = false;
		firstFault = false;
		addTo.incrementGamePoints();
		
		//The score is deuced when the places are at or over (3) 40 points, but they have to be equal
		if((addTo.getGamePoints() == noAdd.getGamePoints()) && (addTo.getGamePoints() >= 3))
		{
			deuce = true;
		}
		else
		{
			deuce = false;
		}
		
		//The player gets the set point if they win over the opponent with out any chance of a deuce
		if(addTo.getGamePoints() == 4 && noAdd.getGamePoints() < 3)
		{
			incrementSetPoints(addTo, noAdd);
			addTo.setGamePoints(0);
			addTo.setGamePoints(0);
		}
		
		//If the players get over the typical game cap (3), then they can only win if they have two over the opponent
		if(addTo.getGamePoints() > 4 && (addTo.getGamePoints() == noAdd.getGamePoints() + TO_WIN_DEUCE) )
		{
			incrementSetPoints(addTo, noAdd);
		}
	}
	
	/**
	 * Adds one set point to addTo's total. Zeros set score and increments match score if set has ended. 
	 * Changes server. Changes ends after odd numbered sets.
	 * @param addTo
	 * 	The player who has scored a point
	 * @param noAdd
	 * 	The other player
	 */
	public void incrementSetPoints(TennisPlayer addTo, TennisPlayer noAdd)
	{
		addTo.incrementSetPoints();
		addTo.setGamePoints(0);
		noAdd.setGamePoints(0);
		
		if(addTo.getSetPoints() >= 6)
		{
			//To win the set and get a match point, the player needs to be at least 2 set points over the opponent
			if(addTo.getSetPoints() - noAdd.getSetPoints() >= ADVANTAGE_SET)
			{
				incrementMatchPoints(addTo, noAdd);
				addTo.setSetPoints(0);
				noAdd.setSetPoints(0);
			}
		}
		//every time a set is score, the servers are changed
		changeServer();
		
		//The ends are only changed when the total set points is an odd number (e.g. 1, 3, 5, etc.)
		if((addTo.getSetPoints() + noAdd.getSetPoints()) % 2 != 0)
		{
			changeEnds();
		}
	}
	
	/**
	 * Adds one match point to addTo's total. Sets game over if match has ended.
	 * @param addTo
	 * 	The player who has scored a point
	 * @param noAdd
	 * 	The other player
	 */
	public void incrementMatchPoints(TennisPlayer addTo, TennisPlayer noAdd)
	{
		addTo.incrementMatchPoints();
		if(addTo.getMatchPoints() == 2 && playBestOfThree)
		{
			gameOver = true;
		}
		else if(addTo.getMatchPoints() == 3)
		{
			gameOver = true;
		}
	}

	/**
	 * Ends the current point early without a point being scored.
	 */
	public void let()
	{
		ballInPlay = false;
		serveStatus = false;
	}
	
	/**
	 * Sets the game score
	 * @param p1Game
	 * 	Player 1's new game score
	 * @param p2Game
	 * 	Player 2's new game score
	 */
	public void setGameScore(int p1Game, int p2Game)
	{
		p1.setGamePoints(p1Game);
		p2.setGamePoints(p2Game);
	}

	/**
	 * Set the set score
	 * @param p1Set
	 * 	Player 1's new set score
	 * @param p2Set
	 * 	Player 2's new set score
	 */	
	public void setSetScore(int p1Set, int p2Set)
	{
		p1.setSetPoints(p1Set);
		p2.setSetPoints(p2Set);
	}
	
	/**
	 * Set the match score
	 * @param p1Match
	 * 	Player 1's new match score
	 * @param p2Match
	 * 	Player 2's new match score
	 */
	public void setMatchScore(int p1Match, int p2Match)
	{
		p1.setMatchPoints(p1Match);
		p2.setMatchPoints(p2Match);
	}

	/**
	 * Set the game, set, and match scores
	 * @param p1Game
	 * 	Player 1's new game score
	 * @param p2Game
	 * 	Player 2's new game score
	 * @param p1Set
	 * 	Player 1's new set score
	 * @param p2Set
	 * 	Player 2's new set score
	 * @param p1Match
	 * 	Player 1's new match score
	 * @param p2Match
	 * 	Player 2's new match score
	 */
	public void setScore(int p1Game, int p2Game, int p1Set, int p2Set, int p1Match, int p2Match)
	{
		setGameScore(p1Game, p2Game);
		setSetScore(p1Set, p2Set);
		setMatchScore(p1Match, p2Match);
	}
	
	/**
	 * Sets the server
	 * @param player
	 * 	the new server
	 */
	public void setServe(int player)
	{
		if(player == 1)
		{
			server = p1.getName();
			return;
		}
		server = p2.getName();
		
	}
	
	/**
	 * Sets the server's end
	 * @param end
	 * 	the new end
	 */
	public void setServerEnd(int end)
	{
		if(server == p1.getName())
		{
			p1.setEnd(end);
		}
		else
		{
			p2.setEnd(end);
		}
	}
}