package com.dabomstew.pkrandom.pokemon;

/*----------------------------------------------------------------------------*/
/*--  Pokemon.java - represents an individual Pokemon, and contains         --*/
/*--                 common Pokemon-related functions.                      --*/
/*--                                                                        --*/
/*--  Part of "Universal Pokemon Randomizer" by Dabomstew                   --*/
/*--  Pokemon and any associated names and the like are                     --*/
/*--  trademark and (C) Nintendo 1996-2012.                                 --*/
/*--                                                                        --*/
/*--  The custom code written here is licensed under the terms of the GPL:  --*/
/*--                                                                        --*/
/*--  This program is free software: you can redistribute it and/or modify  --*/
/*--  it under the terms of the GNU General Public License as published by  --*/
/*--  the Free Software Foundation, either version 3 of the License, or     --*/
/*--  (at your option) any later version.                                   --*/
/*--                                                                        --*/
/*--  This program is distributed in the hope that it will be useful,       --*/
/*--  but WITHOUT ANY WARRANTY; without even the implied warranty of        --*/
/*--  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the          --*/
/*--  GNU General Public License for more details.                          --*/
/*--                                                                        --*/
/*--  You should have received a copy of the GNU General Public License     --*/
/*--  along with this program. If not, see <http://www.gnu.org/licenses/>.  --*/
/*----------------------------------------------------------------------------*/

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Pokemon implements Comparable<Pokemon> {

    public String name;
    public int number;

    public Type primaryType, secondaryType;

    public int hp, attack, defense, spatk, spdef, speed, special;

    public int ability1, ability2, ability3;

    public int catchRate, expYield;

    public int guaranteedHeldItem, commonHeldItem, rareHeldItem, darkGrassHeldItem;

    public int genderRatio;

    public int frontSpritePointer, picDimensions;

    public ExpCurve growthCurve;

    public List<Evolution> evolutionsFrom = new ArrayList<Evolution>();
    public List<Evolution> evolutionsTo = new ArrayList<Evolution>();

    public List<Integer> shuffledStatsOrder = null;
    
    private static final double GENERAL_MEDIAN = 411.5;
    private static final double GENERAL_SD = 108.5;
    private static final double GENERAL_SKEW = -0.1;
    private static final double EVO1_2EVOS_MEDIAN = 300;
    private static final double EVO1_2EVOS_SD = 37;
    private static final double EVO1_2EVOS_SKEW = -0.9;
    private static final double PK_2EVOS_DIFF_MEDIAN = 100;
    private static final double PK_2EVOS_DIFF_SD = 44;
    private static final double PK_2EVOS_DIFF_SKEW = 0.7;
    private static final double EVO1_1EVO_MEDIAN = 310;
    private static final double EVO1_1EVO_SD = 44;
    private static final double EVO1_1EVO_SKEW = -0.6;
    private static final double PK_1EVO_DIFF_MEDIAN = 162.5;
    private static final double PK_1EVO_DIFF_SD = 36;
    private static final double PK_1EVO_DIFF_SKEW = 0.5;
    private static final double NO_EVO_MEDIAN = 487;
    private static final double NO_EVO_SD = 94.0;
    private static final double NO_EVO_SKEW = -0.2;
    private static final double MAX_EVO_MEDIAN = 490;
    private static final double MAX_EVO_SD = 43.5;
    private static final double MAX_EVO_SKEW = -0.3;

    // A flag to use for things like recursive stats copying.
    // Must not rely on the state of this flag being preserved between calls.
    public boolean temporaryFlag;

    public Pokemon() {
        shuffledStatsOrder = Arrays.asList(0, 1, 2, 3, 4, 5);
    }

    public void shuffleStats(Random random) {
        Collections.shuffle(shuffledStatsOrder, random);
        applyShuffledOrderToStats();
    }
    
    public void copyShuffledStatsUpEvolution(Pokemon evolvesFrom) {
        shuffledStatsOrder = evolvesFrom.shuffledStatsOrder;
        applyShuffledOrderToStats();
    }

    private void applyShuffledOrderToStats() {
        List<Integer> stats = Arrays.asList(hp, attack, defense, spatk, spdef, speed);

        // Copy in new stats
        hp = stats.get(shuffledStatsOrder.get(0));
        attack = stats.get(shuffledStatsOrder.get(1));
        defense = stats.get(shuffledStatsOrder.get(2));
        spatk = stats.get(shuffledStatsOrder.get(3));
        spdef = stats.get(shuffledStatsOrder.get(4));
        speed = stats.get(shuffledStatsOrder.get(5));

        // make special the average of spatk and spdef
        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }

    public void randomizeStatsWithinBST(Random random) {
        if (number == 292) {
            // Shedinja is horribly broken unless we restrict him to 1HP.
            int bst = bst() - 51;

            // Make weightings
            double atkW = random.nextDouble(), defW = random.nextDouble();
            double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

            double totW = atkW + defW + spaW + spdW + speW;

            hp = 1;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);

        } else {
            // Minimum 20 HP, 10 everything else
            int bst = bst() - 70;

            // Make weightings
            double hpW = random.nextDouble(), atkW = random.nextDouble(), defW = random.nextDouble();
            double spaW = random.nextDouble(), spdW = random.nextDouble(), speW = random.nextDouble();

            double totW = hpW + atkW + defW + spaW + spdW + speW;

            hp = (int) Math.max(1, Math.round(hpW / totW * bst)) + 20;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);
        }

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsWithinBST(random);
        }

    }

    public void copyRandomizedStatsUpEvolution(Pokemon evolvesFrom) {
        double ourBST = bst();
        double theirBST = evolvesFrom.bst();

        double bstRatio = ourBST / theirBST;

        hp = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.hp * bstRatio)));
        attack = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.attack * bstRatio)));
        defense = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.defense * bstRatio)));
        speed = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.speed * bstRatio)));
        spatk = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spatk * bstRatio)));
        spdef = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spdef * bstRatio)));

        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }
    
    public void randomizeStatsNoRestrictions(Random random, boolean evolutionSanity) {
        double weightSd = 0.16;
        
        if (number == 292) {
            // Shedinja is horribly broken unless we restrict him to 1HP.
            int bst;
            
            if(evolutionSanity) {
                bst = (int) (PK_1EVO_DIFF_MEDIAN + skewedGaussian(random.nextGaussian(), PK_1EVO_DIFF_SKEW) * PK_1EVO_DIFF_SD - 51);
            } else {
                bst = (int) (GENERAL_MEDIAN + skewedGaussian(random.nextGaussian(), GENERAL_SKEW) * GENERAL_SD - 51);
            }
            // Make weightings
            double atkW = Math.max(0, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double defW = Math.max(0, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double spaW = Math.max(0, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double spdW = Math.max(0, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double speW = Math.max(0, Math.min(1, random.nextGaussian() * weightSd + 0.5));

            double totW = atkW + defW + spaW + spdW + speW;

            hp = 1;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);
        } else {            
            // Minimum 20 HP, 10 everything else
            int bst;
            if(evolutionSanity) {
                if(evolutionsFrom.size() > 0) {
                    boolean pk2Evos = false;
                    
                    for (Evolution ev : evolutionsFrom) {
                        // If any of the targets here evolve, the original
                        // Pokemon has 2+ stages.
                        if (ev.to.evolutionsFrom.size() > 0) {
                            pk2Evos = true;
                            break;
                        }
                    }
                    
                    if(pk2Evos) {
                        // First evo of 3 stages
                        bst = (int) (EVO1_2EVOS_MEDIAN + skewedGaussian(random.nextGaussian(), EVO1_2EVOS_SKEW) * EVO1_2EVOS_SD - 70);
                    } else {
                        // First evo of 2 stages
                        bst = (int) (EVO1_1EVO_MEDIAN + skewedGaussian(random.nextGaussian(), EVO1_1EVO_SKEW) * EVO1_1EVO_SD - 70);
                    }
                } else {
                    if(evolutionsTo.size() > 0) {
                        // Last evo, doesn't carry stats
                        bst = (int) (MAX_EVO_MEDIAN + skewedGaussian(random.nextGaussian(), MAX_EVO_SKEW) * MAX_EVO_SD - 70);
                    } else {
                        // No evolutions, no pre-evolutions
                        bst = (int) (NO_EVO_MEDIAN + skewedGaussian(random.nextGaussian(), NO_EVO_SKEW) * NO_EVO_SD - 70);                    
                    }
                }
            } else {
                // No 'Follow evolutions'
                bst = (int) (GENERAL_MEDIAN + skewedGaussian(random.nextGaussian(), GENERAL_SKEW) * GENERAL_SD - 70);
            }
            
            // Make weightings
            double hpW = Math.max(0.01, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double atkW = Math.max(0.01, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double defW = Math.max(0.01, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double spaW = Math.max(0.01, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double spdW = Math.max(0.01, Math.min(1, random.nextGaussian() * weightSd + 0.5));
            double speW = Math.max(0.01, Math.min(1, random.nextGaussian() * weightSd + 0.5));

            double totW = hpW + atkW + defW + spaW + spdW + speW;

            hp = (int) Math.max(1, Math.round(hpW / totW * bst)) + 20;
            attack = (int) Math.max(1, Math.round(atkW / totW * bst)) + 10;
            defense = (int) Math.max(1, Math.round(defW / totW * bst)) + 10;
            spatk = (int) Math.max(1, Math.round(spaW / totW * bst)) + 10;
            spdef = (int) Math.max(1, Math.round(spdW / totW * bst)) + 10;
            speed = (int) Math.max(1, Math.round(speW / totW * bst)) + 10;

            // Fix up special too
            special = (int) Math.ceil((spatk + spdef) / 2.0f);
        }

        // Check for something we can't store
        if (hp > 255 || attack > 255 || defense > 255 || spatk > 255 || spdef > 255 || speed > 255) {
            // re roll
            randomizeStatsNoRestrictions(random, evolutionSanity);
        }
    }
    
    public void copyRandomizedStatsNoRestrictionsUpEvolution(Pokemon evolvesFrom, Random random) {
        double theirBST = evolvesFrom.bst();
        double ourBST;
        double bstRatio;
        
        do {
            if(evolutionsFrom.size() > 0 || (evolutionsTo.get(0).from.evolutionsTo.size() > 0)) {
                // 3 stages
                ourBST = theirBST + PK_2EVOS_DIFF_MEDIAN + skewedGaussian(random.nextGaussian(), PK_2EVOS_DIFF_SKEW) * PK_2EVOS_DIFF_SD;
                System.out.println(name + " is part of evo of 3 stages");
            } else {
                // 2 stages
                ourBST = theirBST + PK_1EVO_DIFF_MEDIAN + skewedGaussian(random.nextGaussian(), PK_1EVO_DIFF_SKEW) * PK_1EVO_DIFF_SD;
                System.out.println(name + " is second evo of 2 stages");
            }
            bstRatio = ourBST / theirBST;
        } while(bstRatio < 1);
        
        hp = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.hp * bstRatio)));
        attack = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.attack * bstRatio)));
        defense = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.defense * bstRatio)));
        speed = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.speed * bstRatio)));
        spatk = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spatk * bstRatio)));
        spdef = (int) Math.min(255, Math.max(1, Math.round(evolvesFrom.spdef * bstRatio)));

        special = (int) Math.ceil((spatk + spdef) / 2.0f);
    }
    
    private double skewedGaussian(double gaussian, double skew) {
        double skewedCdf = ((1 - Math.exp(-1.7 * gaussian * skew)) / (2 * (1 + Math.exp(-1.7 * gaussian * skew))) + 0.5);
        return 2 * gaussian * skewedCdf;
    }

    public int bst() {
        return hp + attack + defense + spatk + spdef + speed;
    }

    public int bstForPowerLevels() {
        // Take into account Shedinja's purposefully nerfed HP
        if (number == 292) {
            return (attack + defense + spatk + spdef + speed) * 6 / 5;
        } else {
            return hp + attack + defense + spatk + spdef + speed;
        }
    }
    
    @Override
    public String toString() {
        return "Pokemon [name=" + name + ", number=" + number + ", primaryType=" + primaryType + ", secondaryType="
                + secondaryType + ", hp=" + hp + ", attack=" + attack + ", defense=" + defense + ", spatk=" + spatk
                + ", spdef=" + spdef + ", speed=" + speed + "]";
    }

    public String toStringRBY() {
        return "Pokemon [name=" + name + ", number=" + number + ", primaryType=" + primaryType + ", secondaryType="
                + secondaryType + ", hp=" + hp + ", attack=" + attack + ", defense=" + defense + ", special=" + special
                + ", speed=" + speed + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + number;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Pokemon other = (Pokemon) obj;
        if (number != other.number)
            return false;
        return true;
    }

    @Override
    public int compareTo(Pokemon o) {
        return number - o.number;
    }
    
    private static final List<Integer> legendaries = Arrays.asList(144, 145, 146, 150, 151, 243, 244, 245, 249, 250,
            251, 377, 378, 379, 380, 381, 382, 383, 384, 385, 386, 479, 480, 481, 482, 483, 484, 485, 486, 487, 488,
            489, 490, 491, 492, 493, 494, 638, 639, 640, 641, 642, 643, 644, 645, 646, 647, 648, 649);

    public boolean isLegendary() {
        return legendaries.contains(this.number);
    }

}
