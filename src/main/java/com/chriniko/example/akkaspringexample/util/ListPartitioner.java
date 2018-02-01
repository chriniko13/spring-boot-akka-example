package com.chriniko.example.akkaspringexample.util;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
public class ListPartitioner {

    public <T> List<List<T>> partition(List<T> records, int partitionSize, boolean equalDistribution) {

        final List<List<T>> result = new ArrayList<>(partitionSize);

        final int subListCount = records.size() / partitionSize;
        final int remainedElements = records.size() % partitionSize;
        final boolean hasRemainedElements = records.size() % partitionSize != 0;

        List<T> subList = new ArrayList<>(subListCount);

        for (int i = 1; i <= records.size(); i++) {

            subList.add(records.get(i - 1));

            if (i % subListCount == 0) {
                result.add(subList);
                subList = new ArrayList<>(subListCount);
            }
        }

        if (hasRemainedElements) { // Note: a case which has not equal job load per partition.

            if (subList.size() != remainedElements) {
                throw new IllegalStateException();
            }

            if (!equalDistribution) { // Note: add all the remained elements to the last partition.
                result.get(partitionSize - 1).addAll(subList);
            } else { //Note: otherwise distribute them equally.

                for (int k = 0, idx = 1; k < subList.size(); k++) {

                    result.get(idx - 1).add(subList.get(k));

                    idx = (idx + 1) % partitionSize;
                }
            }
        }

        return result;
    }


    //TODO remove this and introduce a correct unit test.
    public static void main(String[] args) {

        //494 with 5, equal -> true
        //494 with 5, equal -> false
        //500 with 5, equal -> false
        //500 with 5, equal -> true


        List<Integer> result = IntStream.rangeClosed(1, 494).boxed().collect(Collectors.toList());

        List<List<Integer>> partitionedResult = new ListPartitioner().partition(result, 5, true);

        System.out.println("partitioned result size: " + partitionedResult.size());
        partitionedResult.forEach(r -> System.out.println("result size: " + r.size()));

    }

}
