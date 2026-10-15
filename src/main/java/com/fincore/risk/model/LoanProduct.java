package com.fincore.risk.model;

/**
 * Legacy interface representing a banking loan product.
 *
 * LEGACY PAIN POINT FOR LAB MIGRATION:
 * In Java 8, this is an open interface and callers must use chains of
 * 'if (product instanceof ...)' with manual casts and a fallback 'else'.
 *
 * JAVA 21 TARGET:
 * Convert to a 'sealed interface LoanProduct permits MortgageLoan, ConsumerLoan, SmeLoan, CorporateLineLoan'
 * and convert all implementations to compact Java 21 'record' types.
 */
public interface LoanProduct {

    String getProductCode();

    double getRequestedAmount();

    int getTermMonths();

    int getCreditScore();
}
