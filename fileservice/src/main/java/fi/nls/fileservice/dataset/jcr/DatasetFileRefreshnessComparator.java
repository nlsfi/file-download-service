package fi.nls.fileservice.dataset.jcr;

import java.io.Serializable;
import java.util.Comparator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;

import fi.nls.fileservice.common.DataAccessException;
import fi.nls.fileservice.jcr.MetadataProperty;

@SuppressWarnings("serial")
public class DatasetFileRefreshnessComparator implements Comparator<Node>,
        Serializable {

    @Override
    public int compare(Node node1, Node node2) {

        try {

            int currentComparison = 0;

            if (node1.hasProperty(MetadataProperty.NLS_YEAROFPHOTOGRAPHY)
                    && node2.hasProperty(MetadataProperty.NLS_YEAROFPHOTOGRAPHY)) {
                long year1 = node1.getProperty(
                        MetadataProperty.NLS_YEAROFPHOTOGRAPHY).getLong();
                long year2 = node2.getProperty(
                        MetadataProperty.NLS_YEAROFPHOTOGRAPHY).getLong();

                if (year1 != year2) {
                    currentComparison = year1 > year2 ? -1 : 1;
                }
            }

            if (currentComparison != 0) {
                return currentComparison;
            }

            if (node1.hasProperty(MetadataProperty.NLS_ELEVATIONMODEL)
                    && node2.hasProperty(MetadataProperty.NLS_ELEVATIONMODEL)) {
                String em1 = node1.getProperty(
                        MetadataProperty.NLS_ELEVATIONMODEL).getString();
                String em2 = node2.getProperty(
                        MetadataProperty.NLS_ELEVATIONMODEL).getString();

                // negate: 02m is better than 10m
                currentComparison = -(em1.compareTo(em2));
                if (currentComparison != 0) {
                    return currentComparison;
                }
            }

            if (node1.hasProperty(MetadataProperty.NLS_YEAROFSCANNING)
                    && node2.hasProperty(MetadataProperty.NLS_YEAROFSCANNING)) {
                long year1 = node1.getProperty(
                        MetadataProperty.NLS_YEAROFSCANNING).getLong();
                long year2 = node2.getProperty(
                        MetadataProperty.NLS_YEAROFSCANNING).getLong();

                if (year1 != year2) {
                    currentComparison = year1 > year2 ? -1 : 1;
                }
            }

            if (currentComparison != 0) {
                return currentComparison;
            }

            if (node1.hasProperty(MetadataProperty.NLS_FILEVERSION)
                    && node2.hasProperty(MetadataProperty.NLS_FILEVERSION)) {
                long version1 = node1.getProperty(
                        MetadataProperty.NLS_FILEVERSION).getLong();
                long version2 = node2.getProperty(
                        MetadataProperty.NLS_FILEVERSION).getLong();

                if (version1 != version2) {
                    currentComparison = version1 > version2 ? -1 : 1;
                }
            }

            if (node1.hasProperty(MetadataProperty.NLS_YEAR)
                    && node2.hasProperty(MetadataProperty.NLS_YEAR)) {
                long year1 = node1.getProperty(MetadataProperty.NLS_YEAR)
                        .getLong();
                long year2 = node2.getProperty(MetadataProperty.NLS_YEAR)
                        .getLong();

                if (year1 != year2) {
                    currentComparison = year1 > year2 ? -1 : 1;
                }
            }

            return currentComparison;
        } catch (RepositoryException re) {
            throw new DataAccessException(re);
        }

    }

}
