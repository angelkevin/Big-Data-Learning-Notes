package Leetcode.SwordRefersToAnOffer;

public class offer12 {

    public boolean find(char[][] board, String word, int index,int row,int coloum){

        if(index == word.length())
            return true;

        if(row >= 0 && row < board.length && coloum >= 0 && coloum < board[0].length && board[row][coloum] == word.charAt(index)){

            boolean ans = false;
            char swap = board[row][coloum];
            board[row][coloum] = '*';
            ans = ans || find(board, word,index+1,row-1,coloum);
            ans = ans || find(board, word, index+1,row+1,coloum);
            ans = ans || find(board,word,index+1,row,coloum-1);
            ans = ans || find(board,word,index+1,row,coloum+1);
            board[row][coloum] = swap;
            return ans;
        }

        return false;
    }
    public boolean exist(char[][] board, String word) {

        for(int i = 0;i < board.length;i++){

            for(int j = 0;j < board[0].length;j++){

                if(find(board,word,0,i,j))
                    return true;
            }
        }

        return false;
    }

}
