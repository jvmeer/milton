package com.jsvandermeer;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.SortedSet;

/**
 * Created by Jacob on 3/26/2017.
 */
public class Replication implements Comparable<Replication> {
    private final Specification specification;
    private final ZonedDateTime asOf;
    private final OptionChain.Strip volStrip;
    private final OptionChain.Strip indexFrontStrip;
    private final OptionChain.Strip indexBackStrip;
    private final Chain.Market volFuture;

    Replication(Specification specification, ZonedDateTime asOf, OptionChain.Strip indexFrontStrip,
                OptionChain.Strip indexBackStrip, OptionChain.Strip volStrip, Chain.Market volFuture) {
        this.specification = specification;
        this.asOf = asOf;
        this.volStrip = volStrip;
        this.indexFrontStrip = indexFrontStrip;
        this.indexBackStrip = indexBackStrip;
        this.volFuture = volFuture;
    }

    Basis calculateBasis() {

        return new Basis(calculateForward(indexBackStrip),
                0.0, 0.0, 0.0, 0.0, 0.0);
    }

    private double calculateForward(OptionChain.Strip strip) {
        double minDifference = strip.strikeRange();
        double premiumDifferenceOfMinDifference = minDifference;
        BigDecimal strikeOfMinDifference = null;
        for (BigDecimal strike : strip.getStrikes()) {
            double premiumDifferenceAtStrike = strip.getPremiumDifferenceAtStrike(strike);
            if (premiumDifferenceAtStrike < minDifference) {
                minDifference = Math.abs(premiumDifferenceAtStrike);
                premiumDifferenceOfMinDifference = premiumDifferenceAtStrike;
                strikeOfMinDifference = strike;
            } else {
                break;
            }
        }

        double calendarTimeToExpiry = Utils.calendarPeriod(asOf, strip.getExpiry());

        return strikeOfMinDifference.doubleValue() + Math.exp(Utils.FORWARD_RATE * calendarTimeToExpiry) *
                premiumDifferenceOfMinDifference;
    }



    @Override public boolean equals(Object other) {
        if (!(other instanceof Replication)) return false;
        Replication otherReplication = (Replication) other;
        return (specification.equals(otherReplication.specification));
    }

    @Override public int hashCode() {
        return specification.hashCode();
    }

    @Override public int compareTo(Replication other) {
        return specification.compareTo(other.specification);
    }

    static class Specification implements Comparable<Specification> {
        final Utils.Underlier indexUnderlier;
        final Utils.Underlier volUnderlier;
        final ZonedDateTime volExpiry;
        final ZonedDateTime indexFrontExpiry;
        final ZonedDateTime indexBackExpiry;

        Specification(Utils.Underlier indexUnderlier, Utils.Underlier volUnderlier, ZonedDateTime indexFrontExpiry,
                      ZonedDateTime indexBackExpiry, ZonedDateTime volExpiry) {
            this.indexUnderlier = indexUnderlier;
            this.volUnderlier = volUnderlier;
            this.volExpiry = volExpiry;
            this.indexFrontExpiry = indexFrontExpiry;
            this.indexBackExpiry = indexBackExpiry;
        }

        @Override public boolean equals(Object other) {
            if (!(other instanceof Specification)) return false;
            Specification otherSpecification = (Specification) other;
            return (indexFrontExpiry.equals(otherSpecification.indexFrontExpiry) &&
                    indexBackExpiry.equals(otherSpecification.indexBackExpiry));
        }

        @Override public int hashCode() {
            return indexFrontExpiry.hashCode() * indexBackExpiry.hashCode();
        }

        @Override public int compareTo(Specification other) {
            if (indexFrontExpiry.isBefore(other.indexFrontExpiry)) {
                return -1;
            } else if (indexFrontExpiry.equals(other.indexFrontExpiry)) {
                if (indexBackExpiry.isBefore(other.indexBackExpiry)) {
                    return -1;
                } else if (indexBackExpiry.equals(other.indexBackExpiry)) {
                    return 0;
                } else {
                    return 1;
                }
            } else {
                return 1;
            }
        }
    }

    static class Basis {
        final double indexMid;
        final double volMid;
        final double basis;
        final double indexLowStrike;
        final double indexHighStrike;
        final double volLowStrike;
        final double volHighStrike;

        Basis(double indexMid, double volMid, double indexLowStrike, double indexHighStrike, double volLowStrike,
              double volHighStrike) {
            this.indexMid = indexMid;
            this.volMid = volMid;
            this.basis = volMid - indexMid;
            this.indexLowStrike = indexLowStrike;
            this.indexHighStrike = indexHighStrike;
            this.volLowStrike = volLowStrike;
            this.volHighStrike = volHighStrike;
        }
    }

}
