from gekko import GEKKO
#m = GEKKO()
m = GEKKO(remote=False)
#m = GEKKO(remote=True)
#m.server='http://127.0.0.1'

#x = m.Var(value=0,lb=-5000,ub=5000)
#y = m.Var(value=0,lb=-5000,ub=5000)

x = m.Var()
y = m.Var()


a=2
m.Equation(a*x**2+2*y**2==35)
m.Equation(4*x**2-3*y**2==24)
m.Equation(x>0)
m.Equation(y>0)

m.Obj(x+y)
m.options.IMODE = 3
#m.solve(disp=False)
m.solve()

print('Results')
print('x: ' + str(x.value))
print('y: ' + str(y.value))
