using Microsoft.VisualStudio.TestTools.UnitTesting;
using MyApp.Core;

namespace MyApp.Core.Tests
{
    [TestClass]
    public class CalculatorTests
    {
        private readonly Calculator _calc = new Calculator();

        [TestMethod]
        public void Add_ReturnsSum()
        {
            Assert.AreEqual(5, _calc.Add(2, 3));
        }

        [TestMethod]
        public void Subtract_ReturnsDifference()
        {
            Assert.AreEqual(1, _calc.Subtract(3, 2));
        }

        [TestMethod]
        public void Multiply_ReturnsProduct()
        {
            Assert.AreEqual(6, _calc.Multiply(2, 3));
        }

        [TestMethod]
        public void Divide_ReturnsQuotient()
        {
            Assert.AreEqual(2.5, _calc.Divide(5, 2), 0.0001);
        }

        [TestMethod]
        [ExpectedException(typeof(System.DivideByZeroException))]
        public void Divide_ByZero_Throws()
        {
            _calc.Divide(1, 0);
        }
    }
}