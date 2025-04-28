package com.example.cpsplatform;

public class PagingUtils {

    public static int getStartPage(int currentPage,int parsePage){
        return (currentPage % parsePage == 0) ?
                (currentPage / (parsePage - 1)) * parsePage + 1 :
                (currentPage / parsePage) * parsePage + 1;
    }
    public static int getEndPage(int startPage,int totalPage){
        return Math.min((startPage - 1) + 10, totalPage);
    }
}
