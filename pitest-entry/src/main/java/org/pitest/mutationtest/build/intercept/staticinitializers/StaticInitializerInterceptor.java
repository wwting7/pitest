package org.pitest.mutationtest.build.intercept.staticinitializers;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.pitest.bytecode.analysis.AnalysisFunctions;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.bytecode.analysis.MethodTree;
import org.pitest.classinfo.ClassName;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

/**
 * Identifies and marks mutations in code that is active during class
 * Initialisation.
 *
 * The analysis is simplistic and non-exhaustive. Code is considered to be
 * for static initialisation if it is
 *
 * 1. In a static initializer (i.e <clinit>)
 * 2. In a private static method or constructor called directly from <clinit>
 *
 * TODO A better analysis would include private static methods called indirectly from <clinit>
 * and would exclude methods called from location other than <clinit>.
 *
 */
class StaticInitializerInterceptor implements MutationInterceptor {

  private Predicate<MutationDetails> isStaticInitCode;

  @Override
  public void begin(ClassTree clazz) {
      analyseClass(clazz);
  }

  @Override
  public Collection<MutationDetails> intercept(
      Collection<MutationDetails> mutations, Mutater m) {
    if (this.isStaticInitCode != null) {
      return mutations.stream()
              .filter(this.isStaticInitCode.negate())
              .collect(Collectors.toList());
    }
    return mutations;
  }

  @Override
  public void end() {
    this.isStaticInitCode = null;
  }

  private void analyseClass(ClassTree tree) {
    final Optional<MethodTree> clinit = tree.methods().stream().filter(nameEquals("<clinit>")).findFirst();

    if (clinit.isPresent()) {
      final List<Predicate<MethodTree>> selfCalls =
          clinit.get().instructions().stream()
        .flatMap(is(MethodInsnNode.class))
        .filter(calls(tree.name()))
        .map(StaticInitializerInterceptor::matchesCall)
        .collect(Collectors.toList());

      final Predicate<MethodTree> matchingCalls = Prelude.or(selfCalls);

      final Predicate<MutationDetails> initOnlyMethods = Prelude.or(tree.methods().stream()
      .filter(isPrivateStatic().or(isConstructor()))
      .filter(matchingCalls)
      .map(AnalysisFunctions.matchMutationsInMethod())
      .collect(Collectors.toList())
      );

      this.isStaticInitCode = Prelude.or(isInStaticInitializer(), initOnlyMethods);
    }
  }

  private static Predicate<MutationDetails> isInStaticInitializer() {
    return a -> a.getId().getLocation().getMethodName().equals("<clinit>");
  }

  private static Predicate<MethodTree> isPrivateStatic() {
    return a -> ((a.rawNode().access & Opcodes.ACC_STATIC) != 0)
        && ((a.rawNode().access & Opcodes.ACC_PRIVATE) != 0);
  }

  private Predicate<MethodTree> isConstructor() {
    return a -> a.rawNode().name.equals("<init>");
  }

  private static Predicate<MethodTree> matchesCall(final MethodInsnNode call) {
    return a -> a.rawNode().name.equals(call.name)
        && a.rawNode().desc.equals(call.desc);
  }

  private Predicate<MethodInsnNode> calls(final ClassName self) {
    return a -> a.owner.equals(self.asInternalName());
  }

  private <T extends AbstractInsnNode> Function<AbstractInsnNode,Stream<T>> is(final Class<T> clazz) {
    return a -> {
      if (a.getClass().isAssignableFrom(clazz)) {
        return Stream.of((T)a);
      }
      return Stream.empty();
    };
  }

  private Predicate<MethodTree> nameEquals(final String name) {
    return a -> a.rawNode().name.equals(name);
  }

  @Override
  public InterceptorType type() {
    return InterceptorType.FILTER;
  }

}
