package dev.minechase.core.api.grant.comparator;

import dev.minechase.core.api.grant.grant.Grant;

import java.util.Comparator;

public class GrantComparator implements Comparator<Grant> {

    @Override
    public int compare(Grant g1, Grant g2) {
        int weight = 0;

        weight += Integer.compare(g1.getWeight(), g2.getWeight());
        weight += Long.compare(g2.getTimeLeft(), g1.getTimeLeft());
        weight += Boolean.compare(g1.isRemoved(), g2.isRemoved());

        return weight;
    }

}
