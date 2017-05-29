package com.jsvandermeer;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
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

    Replication(Specification specification, ZonedDateTime asOf, OptionChain.Strip volStrip,
                OptionChain.Strip indexFrontStrip, OptionChain.Strip indexBackStrip, Chain.Market volFuture) {
        this.specification = specification;
        this.asOf = asOf;
        this.volStrip = volStrip;
        this.indexFrontStrip = indexFrontStrip;
        this.indexBackStrip = indexBackStrip;
        this.volFuture = volFuture;
    }

    Basis calculateBasis() {
        return new Basis(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    }

    @Override public boolean equals(Object other) {
        if (!(other instanceof Replication)) return false;
        Replication otherReplication = (Replication) other;
        return (specification.indexFrontExpiry.equals(otherReplication.specification.indexFrontExpiry) &&
                specification.indexBackExpiry.equals(otherReplication.specification.indexBackExpiry));
    }

    @Override public int hashCode() {
        return specification.indexFrontExpiry.hashCode() * specification.indexBackExpiry.hashCode();
    }

    @Override public int compareTo(Replication other) {
        if (specification.indexFrontExpiry.isBefore(other.specification.indexFrontExpiry)) {
            return -1;
        } else if (specification.indexFrontExpiry.equals(other.specification.indexFrontExpiry)) {
            if (specification.indexBackExpiry.isBefore(other.specification.indexBackExpiry)) {
                return -1;
            } else if (specification.indexBackExpiry.equals(other.specification.indexBackExpiry)) {
                return 0;
            } else {
                return 1;
            }
        } else {
            return 1;
        }
    }

    static class Specification {
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
