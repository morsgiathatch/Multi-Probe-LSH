package querying;

import indexing.HashBucket;
import indexing.HashFunction;
import indexing.HashTable;
import indexing.SearchableObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Comparator;


public class PerturbationSequenceMapping {

    private int numberOfHashFunctions;
    private double slotWidth;
    private int numberOfPerturbations;
    private int numberOfHashTables;
    private List<Double> query;
    List<HashTable> hashTables;
    List<SearchableObject> returnSet;
    List<Perturbation> perturbations;

    public PerturbationSequenceMapping(int numberOfHashFunctions, double slotWidth, int numberOfPerturbations, int numberOfHashTables, List<HashTable> hashTables, List<Double> query, List<Perturbation> perturbations)
    {
        this.numberOfHashFunctions = numberOfHashFunctions;
        this.slotWidth = slotWidth;
        this.numberOfPerturbations = numberOfPerturbations;
        this.numberOfHashTables = numberOfHashTables;
        this.hashTables = hashTables;
        this.query = query;
        returnSet = new ArrayList<>();

        this.perturbations = perturbations;
        GenerateMapping();

    }

    public void GenerateMapping()
    {
        Comparator<distances> indexOrder =  new Comparator<distances>() {
            public int compare(distances s1, distances s2) {
                return s1.getIndex() - s2.getIndex();
            }
        };

        for (HashTable hashtable : hashTables)
        {
            distances hashedDistances[] = new distances[2 *this.numberOfHashFunctions];
            HashFunction hashes[] = new HashFunction[this.numberOfHashFunctions];
            List<Integer> hashedQuery = new ArrayList<>();
            int i = 0;
            for (HashFunction hashfunction: hashtable.getHashFunctions())
            {
                hashedQuery.add(hashfunction.getSlotNumber(this.query));
                hashes[i] = hashfunction;
                i++;
            }

            // Add search result of original query.
            returnSet.addAll(hashtable.getObjects(new HashBucket(hashedQuery)));

            int hash_ndx = 0;
            for (int j = 0; j < 2 * this.numberOfHashFunctions; j += 2)
            {
                hashedDistances[j] = new distances(hashes[hash_ndx].getF(this.query) - hashes[hash_ndx].getSlotNumber(this.query)*this.slotWidth, j + 1, -1);
                hashedDistances[j + 1] = new distances(this.slotWidth - hashes[hash_ndx].getF(this.query) + hashes[hash_ndx].getSlotNumber(this.query)*this.slotWidth, j + 2, 1);
                hash_ndx++;
            }

            // Construct sorted array
            distances sortedDistances[] = new distances[2 * this.numberOfHashFunctions];
            for (i = 0; i < 2 * this.numberOfHashFunctions; i++)
                sortedDistances[i] = hashedDistances[i];

            Arrays.sort(sortedDistances);

            for (Perturbation perturbation : this.perturbations)
            {
                List<Integer> indices = perturbation.getPerturbedVector();
//                distances temp_vec[] = new distances[indices.size()];
//
//                for (i = 0; i < indices.size(); i++)
//                {
//                    temp_vec[i] = (sortedDistances[indices.get(i) - 1]);
//                }
//                Arrays.sort(temp_vec, indexOrder);
//
//                for (distances index: temp_vec)
//                {
//                    if (index.getIndex() % 2 == 0) {
//                        hashedQuery.set(index.getIndex()/2 - 1, hashedDistances[index.getIndex() - 1].getDistance());
//                    }
//                    else
//                        hashedQuery.set(index.getIndex()/2, hashedDistances[index.getIndex() - 1].getDistance());
//                }

                for (Integer index : indices)
                {
                    int ndx = sortedDistances[index.intValue()].getIndex();
                    if (ndx % 2 == 0)
                        ndx = ndx/2 - 1;
                    else
                        ndx = ndx/2;
                    int delta = sortedDistances[index.intValue()].getDelta();

                    hashedQuery.set(ndx, hashedQuery.get(ndx) + delta);
                }

                returnSet.addAll(hashtable.getObjects(new HashBucket(hashedQuery)));
            }

        }
    }

    public List<SearchableObject> getQueryResults()
    {
        return this.returnSet;
    }


    private class distances implements Comparable<distances> {

        private double distance;
        private int index;
        private int delta;

        public distances(double distance, int index, int delta) {
            this.distance = distance;
            this.index = index;
            this.delta = delta;
        }

        @Override
        public int compareTo(distances other) {
            return Double.valueOf(this.distance).compareTo(Double.valueOf(other.distance));
        }

        public double getDistance() {
            return this.distance;
        }

        public int getIndex() {
            return this.index;
        }

        public int getDelta() {return this.delta; }

    }


}
