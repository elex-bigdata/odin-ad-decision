package com.elex.research;

import org.la4j.LinearAlgebra;
import org.la4j.factory.CRSFactory;
import org.la4j.factory.Factory;
import org.la4j.matrix.Matrices;
import org.la4j.matrix.Matrix;
import org.la4j.matrix.sparse.SparseMatrix;

import java.util.Random;

/**
 * Author: liqiang
 * Date: 14-10-27
 * Time: 下午4:44
 */
public class La4jTest {

    public static void main(String[] args){

        int i = 1000000;
        int j = 2000000;

        Matrices.asRandomSource(100,2000);

        Factory factory = new CRSFactory();

        SparseMatrix fa = (SparseMatrix)factory.createMatrix( Matrices.asRandomSource(200,3000));
        SparseMatrix wf = (SparseMatrix)factory.createMatrix( Matrices.asRandomSource(1,200));

        long begin = System.currentTimeMillis();
        Matrix result = wf.multiply(fa);
        System.out.println(System.currentTimeMillis() - begin);
        System.out.println(result.toString());



    }
}
