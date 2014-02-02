/**
 * Copyright  2013  Temur Vibliani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.gveli.hwsom.control;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author TEMUR
 */
public class CyclicRandomIndexGenerator {

    private Random r;
    private int range;
    private ArrayList<Integer> indexList;

    public CyclicRandomIndexGenerator(int range) {
        r = new Random();
        this.range = range;
        initializeIndexList();
    }

    private void initializeIndexList() {
        indexList = new ArrayList<>(range);
        for (int i = 0; i < range; i++) {
            indexList.add(i);
        }
    }

    public int nextRandomIndex() {
        if (indexList.isEmpty()) {
            initializeIndexList();
        }
        return indexList.remove(r.nextInt(indexList.size()));
    }
}
