package org.github.muhatashim.translator.java;

import java.lang.reflect.InvocationTargetException;

public class TranslatorTest {

    public static void main(String[] args) throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        A a = new A();
        B b = new B();

        Translator<A, B> translator = new Translator<>(A.class, B.class);

        translator.addTranslation(A::getA, B::getA, A::setA, B::setA, String::valueOf, Integer::parseInt);
        translator.addTranslation(A::getB, B::getB, A::setB, B::setB);
        translator.addTranslation(A::getC, B::getC, A::setC, B::setC,
                integer -> String.valueOf(integer).charAt(0),
                it -> Integer.parseInt(String.valueOf(it)));


        a.setA("1");
        a.setB(2);
        a.setC('3');

        b.setA(4);
        b.setB(5);
        b.setC(6);
        System.out.println(translator.translateToRight(a)); //returns B{a=1, b=2, c=3}
        System.out.println(translator.translateToLeft(b)); //returns A{a='4', b=5, c=6}
    }

    public static class B {
        int a;
        int b;
        int c;

        @Override
        public String toString() {
            return "B{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public int getA() {
            return a;
        }

        public void setA(int a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public int getC() {
            return c;
        }

        public void setC(int c) {
            this.c = c;
        }
    }

    public static class A {
        String a;
        int b;
        char c;

        @Override
        public String toString() {
            return "A{" +
                    "a='" + a + '\'' +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }

        public String getA() {
            return a;
        }

        public void setA(String a) {
            this.a = a;
        }

        public int getB() {
            return b;
        }

        public void setB(int b) {
            this.b = b;
        }

        public char getC() {
            return c;
        }

        public void setC(char c) {
            this.c = c;
        }
    }
}
