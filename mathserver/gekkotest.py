from gekko import GEKKO
m = GEKKO()

x1 = m.Var(value=0,lb=0,ub=5000)
x2 = m.Var(value=0,lb=0,ub=5000)
x3 = m.Var(value=0,lb=0,ub=5000)
x4 = m.Var(value=0,lb=0,ub=5000)

m.Equation(x1*x2*x3*x4>=25)
m.Equation(x1**2+x2**2+x3**2+x4**2==40)

m.Obj(x1*x4*(x1+x2+x3)+x3)
m.options.IMODE = 3
m.solve()

print('Results')
print('x1: ' + str(x1.value))
print('x2: ' + str(x2.value))
print('x3: ' + str(x3.value))
print('x4: ' + str(x4.value))