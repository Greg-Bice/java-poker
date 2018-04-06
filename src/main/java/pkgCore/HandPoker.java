package pkgCore;

import java.util.ArrayList;
import java.util.Collections;
import pkgConstants.*;
import pkgEnum.eCardNo;
import pkgEnum.eHandStrength;
import pkgEnum.eRank;
import pkgEnum.eSuit;

public class HandPoker extends Hand {

	private ArrayList<CardRankCount> CRC = null;

	public HandPoker() {
		this.setHS(new HandScorePoker());
	}

	protected ArrayList<CardRankCount> getCRC() {
		return CRC;
	}

	@Override
	public HandScore ScoreHand() {
		// TODO : Implement this method... call each of the 'is' methods (isRoyalFlush,
		// etc) until
		// one of the hands is true, then score the hand

		Collections.sort(super.getCards());
		Frequency();

		ArrayList<Card> hand = super.getCards();
		
		if (isRoyalFlush()) {
			HandPower( eHandStrength.RoyalFlush, hand.get(0), hand.get(4) );
		} else if (isStraightFlush()) {
			HandPower( eHandStrength.StraightFlush, hand.get(0), hand.get(4) );
		} else if ( isFourOfAKind() ) {
			HandPower( eHandStrength.FourOfAKind, hand.get(1), null );
		} else if ( isFullHouse() ) {
			HandPower( eHandStrength.FullHouse, hand.get( 0 ), hand.get( 3 ) );
		} else if ( isFlush() ) {
			HandPower( eHandStrength.Flush, hand.get(0), hand.get(4) );
		} else if ( isStraight() ) {
			HandPower( eHandStrength.Straight, hand.get(0), hand.get(4));
		} else if ( isThreeOfAKind() ) {
			// In function
		} else if ( isTwoPair() ) {
			HandPower( eHandStrength.TwoPair, hand.get( 1 ), hand.get( 3 ) );
		} else if ( isPair() ) {
			// In function
		} else if ( isHighCard() ) {
			HandPower( eHandStrength.HighCard, hand.get(0), null );
		}

		return this.getHS();
	}
	
	public void HandPower( eHandStrength str, Card hi, Card lo ) {
		
		HandScorePoker HSP = (HandScorePoker) this.getHS();
		HSP.seteHandStrength( str );
		HSP.setHiCard(hi);
		HSP.setLoCard(lo);
		HSP.setKickers(FindTheKickers(this.getCRC()));
		this.setHS(HSP);
		
	}

	public boolean isRoyalFlush() {
		boolean bIsRoyalFlush = false;		
		
		ArrayList<Card> hand = super.getCards();
		bIsRoyalFlush = isStraightFlush() && hand.get(0).geteRank() == eRank.KING;
		
		return bIsRoyalFlush;
	}

	public boolean isStraightFlush() {
		boolean bisStraightFlush = false;
		
		ArrayList<Card> hand = super.getCards();
		
		bisStraightFlush = isFlush() && isStraight();
		
		return bisStraightFlush;
	}
	
	public boolean isFourOfAKind() {
		boolean bisFourOfAKind = false;
		
		ArrayList<Card> hand = super.getCards();
		bisFourOfAKind = CountRank( hand.get(1).geteRank() ) == 4;
	
		return bisFourOfAKind;
	}

	public boolean isFullHouse() {
		boolean bisFullHouse = false;
		
		ArrayList<Card> hand = super.getCards();
		if ( this.getCRC().size() == 2 ) {
			
			if ( ( CountRank( hand.get(0).geteRank() ) == 3 && CountRank( hand.get(3).geteRank() ) == 2 ) ) {
				bisFullHouse = true;
			} else if ( ( CountRank( hand.get(0).geteRank() ) == 2 && CountRank( hand.get(3).geteRank() ) == 3 ) ) {
				bisFullHouse = true;
			}
			
		}
		
		return bisFullHouse;

	}

	public boolean isFlush() {
		boolean bisFlush = false;

		int iCardCnt = super.getCards().size();
		int iSuitCnt = 0;

		for (eSuit eSuit : eSuit.values()) {
			for (Card c : super.getCards()) {
				if (eSuit == c.geteSuit()) {
					iSuitCnt++;
				}
			}
			if (iSuitCnt > 0)
				break;
		}

		if (iSuitCnt == iCardCnt)
			bisFlush = true;
		else
			bisFlush = false;

		return bisFlush;
	}

	public boolean isStraight() {
		boolean bisStraight = true;
		
		ArrayList<Card> hand = super.getCards();
	
		for ( int i = 0; i < hand.size() - 1; i++ ) {
			
			int cur = hand.get( i ).geteRank().getiCardValue();
			
			if ( cur + 1 != hand.get( i + 1 ).geteRank().getiCardValue()) {
				bisStraight = false;
				break;
			}
			
		}
		
		if ( bisStraight ) {
			HandPower( eHandStrength.Straight, hand.get(0), hand.get(4) );
		}
		
		
		return bisStraight;
	}

	public boolean isThreeOfAKind() {
		boolean bisThreeOfAKind = false;
		if (this.getCRC().size() == 3) {
			if (this.getCRC().get(0).getiCnt() == Constants.THREE_OF_A_KIND) {
				HandScorePoker HSP = (HandScorePoker) this.getHS();
				HSP.seteHandStrength(eHandStrength.ThreeOfAKind);
				int iGetCard = this.getCRC().get(0).getiCardPosition();
				HSP.setHiCard(this.getCards().get(iGetCard));
				HSP.setLoCard(null);
				HSP.setKickers(FindTheKickers(this.getCRC()));
				this.setHS(HSP);
			}
		}
		return bisThreeOfAKind;
	}

	public boolean isTwoPair() {
		boolean bisTwoPair = false;
		
		ArrayList<Card> hand = super.getCards();
		
		if ( this.getCRC().size() == 3 ) {
			
			if ( ( ( hand.get( 1 ).geteRank() == hand.get( 0 ).geteRank() ) && ( hand.get( 3 ).geteRank() == hand.get( 2 ).geteRank() ) )
				|| ( ( hand.get( 1 ).geteRank() == hand.get( 2 ).geteRank() ) && ( hand.get( 3 ).geteRank() == hand.get( 4 ).geteRank() ) ) )
			{				
				bisTwoPair = true;
			}
		}
		
		return bisTwoPair;
	}

	public boolean isPair() {
		boolean bisPair = false;
		
		ArrayList<Card> hand = super.getCards();
		
		if ( this.getCRC().size() == 4 ) { 
			
			for ( int i = 0; i < hand.size() - 1; i++ ) {
				if ( hand.get(i).geteRank() == hand.get( i + 1 ).geteRank() ) {
					bisPair = true;
					HandPower( eHandStrength.Pair, hand.get( i ), null );
					break;
				}
			}
			
		}
		
		return bisPair;
	}

	public boolean isHighCard() {
		boolean bisHighCard = true;
		
		ArrayList<Card> hand = super.getCards();
		
		return bisHighCard;
	}

	private ArrayList<Card> FindTheKickers(ArrayList<CardRankCount> CRC) {
		ArrayList<Card> kickers = new ArrayList<Card>();

		for (CardRankCount crcCheck : CRC) {
			if (crcCheck.getiCnt() == 1) {
				kickers.add(this.getCards().get(crcCheck.getiCardPosition()));
			}
		}

		return kickers;
	}

	private void Frequency() {
		CRC = new ArrayList<CardRankCount>();
		int iCnt = 0;
		int iPos = 0;
		for (eRank eRank : eRank.values()) {
			iCnt = (CountRank(eRank));
			if (iCnt > 0) {
				iPos = FindCardRank(eRank);
				CRC.add(new CardRankCount(eRank, iCnt, iPos));
			}
		}
		Collections.sort(CRC);
	}

	private int CountRank(eRank eRank) {
		int iCnt = 0;
		for (Card c : super.getCards()) {
			if (c.geteRank() == eRank) {
				iCnt++;
			}
		}
		return iCnt;
	}

	private int FindCardRank(eRank eRank) {
		int iPos = 0;

		for (iPos = 0; iPos < super.getCards().size(); iPos++) {
			if (super.getCards().get(iPos).geteRank() == eRank) {
				break;
			}
		}
		return iPos;
	}

}
