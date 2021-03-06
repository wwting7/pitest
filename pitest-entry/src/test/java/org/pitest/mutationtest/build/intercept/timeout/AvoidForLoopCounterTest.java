package org.pitest.mutationtest.build.intercept.timeout;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.intercept.javafeatures.FilterTester;
import org.pitest.mutationtest.engine.gregor.config.Mutator;
import org.pitest.mutationtest.engine.gregor.mutators.IncrementsMutator;

public class AvoidForLoopCounterTest {

  AvoidForLoopCounterFilter testee = new AvoidForLoopCounterFilter();
  private static final String             PATH      = "forloops/{0}_{1}";
  FilterTester verifier = new FilterTester(PATH, this.testee, IncrementsMutator.INCREMENTS);


  @Test
  public void shouldDeclareTypeAsFilter() {
    assertThat(this.testee.type()).isEqualTo(InterceptorType.FILTER);
  }

  @Test
  public void shouldNotFilterMutantsWhenNoLoopPresent() {
    final FilterTester verifier = new FilterTester(PATH, this.testee, Mutator.all());
    verifier.assertFiltersNMutationFromSample(0, "IHaveNoLoops");
  }

  @Test
  public void shouldNotFilterIncrementMutantsInConditions() {
    final FilterTester verifier = new FilterTester(PATH, this.testee, Mutator.all());
    verifier.assertFiltersNMutationFromClass(0, HasIncrementsInIfs.class);
  }

  @Test
  public void shouldFilterMutationsThatRemoveForLoopIncrement() {
    this.verifier.assertFiltersNMutationFromSample(1, "HasAForLoop");
  }

  @Test
  public void shouldNotFilterOtherIncrementMutationsInForLoop() {
    this.verifier.assertFiltersNMutationFromSample(1, "HasAForLoopAndOtherIncrements");
  }

  @Test
  public void shouldFilterIncrementMutantInListIterationByIndex() {
    this.verifier.assertFiltersNMutationFromSample(1, "HasForLoopOverList");
  }

  @Test
  public void shouldFilterIncrementMutantsWhenLoopEndRetreivedFromFieldMethodCall() {
    this.verifier.assertFiltersNMutationFromSample(1, "HasForLoopOverListStoredAsField");
  }

  @Test
  public void shouldFilterIncrementsInArrayLoop() {
    this.verifier.assertFiltersNMutationFromSample(1, "HasArrayIteration");
  }

  @Test
  public void shouldFilterLoopsWithLessThanClause() {
    this.verifier.assertFiltersNMutationFromClass(1, LessThanLoop.class);
  }

  @Test
  public void shouldFilterLoopsWithSmallConstant() {
    this.verifier.assertFiltersNMutationFromClass(1, SmallConstantLoop.class);
  }

  @Test
  public void shouldFilterReverseLoops() {
    this.verifier.assertFiltersNMutationFromClass(1, ReverseLoop.class);
  }

  @Test
  public void shouldFilterLoopsWithoutInitialiser() {
    this.verifier.assertFiltersNMutationFromClass(1, ReverseNoInitialiseLoop.class);
  }

  static class ReverseNoInitialiseLoop {
    void foo(int i) {
      for (; i > 0; i--) {
        System.out.println("" + i);
      }
    }
  }

  static class ReverseLoop {
    void foo() {
      for (int i = 9; i > 0; i--) {
        System.out.println("" + i);
      }
    }
  }

  static class LessThanLoop {
    void foo() {
      for (int i = 0; i < 10; i++) {
        System.out.println("" + i);
      }
    }
  }

  static class SmallConstantLoop {
    void foo() {
      for (int i = 0; i < 2; i++) {
        System.out.println("" + i);
      }
    }
  }

  static class IHaveNoLoops {
    void foo(boolean b) {
      if ( b ) {
        System.out.println("Loop free");
      }
    }
  }

  static class HasIncrementsInIfs {
    void foo(int i) {
      i = i ++;
      if ( i > 10 ) {
        System.out.println("Loop free");
      }
    }
  }

  static class HasAForLoopAndOtherIncrements {
    void foo() {
      int j = 0;
      for (int i = 0; i != 10; i++) {
        j++;
        System.out.println("" + j);
      }
    }
  }

  static class HasAForLoop {
    void foo() {
      for (int i = 0; i != 10; i++) {
        System.out.println("" + i);
      }
    }
  }

  static class HasForLoopOverList {
    void foo(List<Integer> is) {
      for (int i = 0; i != is.size(); i++) {
        System.out.println("" + i);
      }
    }
  }

  static class HasForLoopOverListStoredAsField {
    List<Integer> is;
    void foo() {
      for (int i = 0; i != this.is.size(); i++) {
        System.out.println("" + i);
      }
    }
  }

  static class HasArrayIteration {
    void foo(int[] is) {
      for (int i = 0; i != is.length; i++) {
        System.out.println("" + i);
      }
    }
  }

}


