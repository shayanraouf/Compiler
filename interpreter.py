#! /c/Python27/python -tt

"""interpreter.py: Simple Stack-Based VM Interpreter.

Pass your VM assembly language into the VM as standard input.

Version 2: added branch_negative

Version 3: Add integer comparision mnemonics
           Add branch true/false mnemonics
           correct "dropf" key.
           Disable fire hose: Add DEBUG_OUTPUT variable with default value of false.
           Disable selected PyLint warnings. (File really needs to be scrubbed)
"""

__author__ = 'Author: Morris Bernstein (morris@systems-deployment.com)'
__copyright__ = 'Copyright 2017 Systems Deployment, LLC'

import fileinput
import math
import re
import sys

DEBUG_OUTPUT = False

#pylint: disable=W0311,C0301,E1101,C0111,C0103

class Error(Exception):
    pass

class ParseError(Error):
    pass

class DuplicateLabelError(Error):
    pass

class StackError(Error):
    pass


class Stack(object):
    def __init__(self):
        self.data = []

    def __len__(self):
        return len(self.data)

    def push(self, value):
        self.data.append(value)

    def pop_int(self):
        value =  self.data.pop()
        if type(value) != int:
            raise StackError('expected int')
        return value

    def pop_int2(self):
        value2 = self.pop_int()
        value1 = self.pop_int()
        return value1, value2

    def pop_float(self):
        value =  self.data.pop()
        if type(value) != float:
            raise StackError('expected float')
        return value

    def pop_float2(self):
        value2 = self.pop_float()
        value1 = self.pop_float()
        return value1, value2

    def get_int(self, depth):
        value = self.data[-1 - depth]
        if type(value) != int:
            raise StackError('expected int')
        return value

    def get_float(self, depth):
        value = self.data[-1 - depth]
        if type(value) != float:
            raise StackError('expected int')
        return value

    def dump(self):
        for i, x in enumerate(self.data[::-1]):
            sys.stderr.write("stack(%d): %s\n" % (i, x))

class VM(object):
    def __init__(self):
        self.program_counter = 0
        self.memory = []
        self.call_stack = Stack()
        self.stack = Stack()
        self.labels = {}
        self.opcodes = {
            'nop': (0, self.nop, "--", 'no operation'),
            'alloc_int': (0, self.alloc_int, 'n -- address', 'allocate n int values in memory'),
            'alloc_float': (0, self.alloc_float, 'n -- address', 'allocate n floating-point values in memory'),
            'int_literal': (1, self.int_literal, '-- ', 'emit int value in instruction stream'),
            'float_literal': (1, self.float_literal, '-- x', 'emit floating-point value in instruction stream'),
            'exit': (0, self.exit, 'n --', 'exit with status n'),
            'print_byte': (0, self.print_byte, 'n --', 'print character'),
            'print_int': (0, self.print_int, 'n --', 'format and print int value'),
            'print_float': (0, self.print_float, 'x --', 'format and print floating-point value'),
            'branch': (0, self.branch, 'address --', 'branch to address'),
            'branch_true': (0, self.branch_nonzero, 'flag address --', 'branch to address if flag (from comparision) is true'),
            'branch_false': (0, self.branch_zero, 'flag address --', 'branch to address if flag (from comparision) is false'),
            'branch_zero': (0, self.branch_zero, 'flag address --', 'branch to address if flag is zero'),
            'branch_nonzero': (0, self.branch_nonzero, 'flag address --', 'branch to address if flag is nonzero'),
            'branch_negative': (0, self.branch_negative, 'flag address --', 'branch to address if flag is negative'),
            'call': (0, self.call, 'address --', 'branch to subroutine'),
            'return': (0, self.return_, '--', 'return from subroutine'),
            'load0': (0, self.load0, '-- 0', 'push zero onto the stack'),
            'load1': (0, self.load1, '-- 1', 'push one onto the stack'),
            'load_mem_int': (0, self.load_mem_int, 'address -- n', 'load int value from memory'),
            'load_mem_float': (0, self.load_mem_float, 'address -- x', 'load floating-point value from memory'),
            'load_stack_int': (0, self.load_stack_int, 'address -- n', 'load int value from the stack'),
            'load_stack_float': (0, self.load_stack_float, 'address -- x', 'load floating-point value from the stack'),
            'load_label': (1, self.make_load_label, '-- address', 'push address onto the stack'),
            'load_sp': (0, self.load_sp, '-- sp', 'push value of stack pointer onto stack'),
            'pop_frame': (0, self.pop_frame, 'address --', 'reset the stack pointer'),
            'store_mem_int': (0, self.store_mem_int, 'n address --', 'store integer value into memory'),
            'store_mem_float': (0, self.store_mem_float, 'x address --', 'store floating-point value into memory'),
            'store_stack_int': (0, self.store_stack_int, 'n address --', 'store integer value into the stack'),
            'store_stack_float': (0, self.store_stack_float, 'x address --', 'store floating-point value into the stack'),
            'drop': (0, self.drop, 'n --', 'remove top int from the stack'),
            'dropf': (0, self.drop, 'n --', 'remove top floating-point value from the stack'),
            'dup': (0, self.dup, 'n -- n n', 'duplicate the top int on the stack'),
            'dupf': (0, self.dupf, 'x -- x x', 'duplicate the top floating-point on the stack'),
            'dup2': (0, self.dup2, 'n m -- n m n m', 'duplicate the top two ints on the stack'),
            'dup2f': (0, self.dup2, 'x y -- x y x y', 'duplicate the top two floats on the stack'),
            'dup_if0': (0, self.dup_if0, 'n 0 -- n n OR n m -- n', 'duplicate int if flag is zero'),
            'swap': (0, self.swap, 'n m -- m n', 'swap the top two int elements on the stack'),
            'swapf': (0, self.swap, 'x y -- x y', 'swap the top two float elements on the stack'),
            'rotate': (0, self.rotate, 'n1 n2 n3 -- n2 n3 n1', 'rotate the top 3 ints on the stack'),
            'rotatef': (0, self.rotate, 'x y z -- y z x', 'rotate the top 3 floats on the stack'),
            'roll': (0, self.roll, 'n1 n2 n3 -- n3 n1 n2', 'roll the top 3 ints on the stack'),
            'rollf': (0, self.roll, 'x y z  -- z x y', 'roll the top 3 floats on the stack'),
            'over': (0, self.over, 'n m -- n m n', 'push the second int element on the the stack'),
            'overf': (0, self.over, 'x y -- x y x', 'push the second float element on the the stack'),
            'pick': (0, self.pick, 'an an-1 .. a0 i -- an an-1 .. a0 ai', 'pick int element relative to top of stack'),
            'pickf': (0, self.pickf, 'an an-1 .. a0 i -- an an-1 .. a0 ai', 'pick float element relative to top of stack'),
            'and': (0, self.and_, 'n1 n2 -- m', 'bitwise and'),
            'or': (0, self.or_, 'n1 n2 -- m', 'bitwise or'),
            'xor': (0, self.xor, 'n1 n2 -- m', 'bitwise exclusive or'),
            'not': (0, self.not_, 'n -- m', 'bitwise complement'),
            'shift_right': (0, self.shift_right, 'n -- m', 'signed logical shift right'),
            'shift_left': (0, self.shift_left, 'n -- m', 'logical shift left'),
            'add': (0, self.add, 'n m -- n+m', 'int addition'),
            'add_f': (0, self.add_f, 'x y -- x+y', 'floating-point addition'),
            'sub': (0, self.sub, 'n m -- n-m', 'int subtraction'),
            'sub_f': (0, self.sub_f, 'x y -- x-y', 'floating-point subtraction'),
            'mul': (0, self.mul, 'n m -- n*m', 'int multiplication'),
            'mul_f': (0, self.mul_f, 'x y -- x*y', 'floating-point multiplication'),
            'div': (0, self.div, 'n m -- n/m', 'int division'),
            'div_f': (0, self.div_f, 'x y -- x/y', 'floating-point division'),
            'lt': (0, self.lt, 'x y -- flag', 'floating-point less-than x < y'),
            'le': (0, self.le, 'x y -- flag', 'floating-point less-than-or-equals x <= y'),
            'eq': (0, self.eq, 'x y -- flag', 'floating-point equals x == y'),
            'ge': (0, self.ge, 'x y -- flag', 'floating-point greater-than-or-equals x >= y'),
            'gt': (0, self.gt, 'x y -- flag', 'floating-point greater-than x > y'),
            'ne': (0, self.ne, 'x y -- flag', 'floating-point not-equals x <> y'),
            'lt_f': (0, self.lt_f, 'x y -- flag', 'floating-point less-than x < y'),
            'le_f': (0, self.le_f, 'x y -- flag', 'floating-point less-than-or-equals x <= y'),
            'eq_f': (0, self.eq_f, 'x y -- flag', 'floating-point equals x == y'),
            'ge_f': (0, self.ge_f, 'x y -- flag', 'floating-point greater-than-or-equals x >= y'),
            'gt_f': (0, self.gt_f, 'x y -- flag', 'floating-point greater-than x > y'),
            'ne_f': (0, self.ne_f, 'x y -- flag', 'floating-point not-equals x <> y'),
            'is_inf': (0, self.is_inf, 'x -- flag', 'floating-point x is positive or negative infinity'),
            'is_nan': (0, self.is_nan, 'x -- flag', 'floating-point x is not-a-number (NaN)'),
            'to_float': (0, self.to_float, 'n -- x', 'int to floating-point value'),
            'to_int': (0, self.to_int, 'x -- n', 'floating-point to int value'),
        }

    def nop(self):
        pass

    def alloc_int(self):
        n = self.stack.pop_int()
        p = len(self.stack)
        self.memory.extend([0] * n)
        self.stack.push(p)

    def alloc_float(self):
        n = self.stack.pop_int()
        p = len(self.stack)
        self.memory.extend([0.0] * n)
        self.stack.push(p)

    def int_literal(self, literal):
        if literal.startswith("0x"):
            return int(literal, 16)
        else:
            return int(literal)

    def float_literal(self, literal):
        return float(literal)

    def exit(self):
        sys.exit(self.stack.pop_int())

    def print_byte(self):
        sys.stdout.write(chr(self.stack.pop_int() & 0xff))

    def print_int(self):
        sys.stdout.write(str(self.stack.pop_int()))

    def print_float(self):
        sys.stdout.write(str(self.stack.pop_float()))

    def branch(self):
        self.program_counter = self.stack.pop_int()

    def branch_zero(self):
        target = self.stack.pop_int()
        self.value = self.stack.pop_int()
        if self.value == 0:
            self.program_counter = target

    def branch_nonzero(self):
        target = self.stack.pop_int()
        self.value = self.stack.pop_int()
        if self.value != 0:
            self.program_counter = target

    def branch_negative(self):
        target = self.stack.pop_int()
        self.value = self.stack.pop_int()
        if self.value < 0:
            self.program_counter = target

    def call(self):
        target = self.stack.pop_int()
        self.call_stack.push(self.program_counter)
        self.program_counter = target

    def return_(self):
        self.program_counter = self.call_stack.pop_int()

    def load0(self):
        self.stack.push(0)

    def load1(self):
        self.stack.push(1)

    def load_mem_int(self):
        address = self.stack.pop_int()
        value = self.memory[address]
        if type(value) != int:
            raise StackError('expected int')
        self.stack.push(value)

    def load_mem_float(self):
        address = self.stack.pop_int()
        value = self.memory[address]
        if type(value) != float:
            raise StackError('expected int')
        self.stack.push(value)

    def load_stack_int(self, value):
        address = self.stack.pop_int()
        value = self.stack.data[address]
        if type(value) != int:
            raise StackError('expected int')
        self.stack.push(value)

    def load_stack_float(self, value):
        address = self.stack.pop_int()
        value = self.stack.data[address]
        if type(value) != float:
            raise StackError('expected int')
        self.stack.push(value)

    def make_load_label(self, label):
        def push_label():
            self.stack.push(self.labels[label])
        return push_label

    def load_sp(self):
        sp = len(self.stack)
        self.stack.push(sp)

    def pop_frame(self):
        frame_pointer = self.data.pop_int()
        del self.stack.data[frame_pointer]

    def store_mem_int(self):
        address = self.stack.pop_int()
        value = self.stack.pop_int()
        self.memory[address] = value

    def store_mem_float(self):
        address = self.stack.pop_int()
        value = self.stack.pop_float()
        self.memory[address] = value

    def store_stack_int(self):
        address = self.stack.pop_int()
        value = self.stack.pop_int()
        self.stack.data[address] = value

    def store_stack_float(self):
        address = self.stack.pop_int()
        value = self.stack.pop_float()
        self.stack.data[address] = value

    def drop(self):
        self.stack.pop_int()

    def dropf(self):
        self.stack.pop_float()

    def dup(self):
        value = self.stack.pop_int()
        self.stack.push(value)
        self.stack.push(value)

    def dupf(self):
        value = self.stack.pop_float()
        self.stack.push(value)
        self.stack.push(value)

    def dup2(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1)
        self.stack.push(value2)
        self.stack.push(value1)
        self.stack.push(value2)

    def dup2f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(value1)
        self.stack.push(value2)
        self.stack.push(value1)
        self.stack.push(value2)

    def dup_if0(self):
        flag = self.stack.pop_int()
        if flag == 0:
            value = self.stack.pop_int()
            self.stack.push(value)
            self.stack.push(value)

    def swap(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value2)
        self.stack.push(value1)

    def swapf(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(value2)
        self.stack.push(value1)

    def rotate(self):
        value3 = self.stack.pop_int()
        value2 = self.stack.pop_int()
        value1 = self.stack.pop_int()
        self.stack.push(value2)
        self.stack.push(value3)
        self.stack.push(value1)

    def rotatef(self):
        value3 = self.stack.pop_float()
        value2 = self.stack.pop_float()
        value1 = self.stack.pop_float()
        self.stack.push(value2)
        self.stack.push(value3)
        self.stack.push(value1)

    def roll(self):
        value3 = self.stack.pop_int()
        value2 = self.stack.pop_int()
        value1 = self.stack.pop_int()
        self.stack.push(value3)
        self.stack.push(value1)
        self.stack.push(value2)

    def rollf(self):
        value3 = self.stack.pop_float()
        value2 = self.stack.pop_float()
        value1 = self.stack.pop_float()
        self.stack.push(value3)
        self.stack.push(value1)
        self.stack.push(value2)

    def over(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1)
        self.stack.push(value2)
        self.stack.push(value1)

    def overf(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1)
        self.stack.push(value2)
        self.stack.push(value1)

    def pick(self):
        depth = self.stack.pop_int()
        value = self.stack.get_int(depth)
        self.stack.push(value)

    def pickf(self):
        depth = self.stack.pop_int()
        value = self.stack.get_float(depth)
        self.stack.push(value)

    def and_(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1 & value2)

    def or_(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1 | value2)

    def xor(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1 ^ value2)

    def not_(self):
        value1 = self.stack.pop_int()
        self.stack.push(~value1)

    def shift_right(self):
        value, n = self.stack.pop_int2()
        self.stack.push(value >> n)

    def shift_left(self):
        value, n = self.stack.pop_int2()
        self.stack.push(value << n)

    def add(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1 + value2)

    def add_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(value1 + value2)

    def sub(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1 - value2)

    def sub_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(value1 - value2)

    def mul(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1 * value2)

    def mul_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(value1 * value2)

    def div(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(value1 / value2)

    def div_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(value1 / value2)

    def lt(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(int(value1 < value2))

    def le(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(int(value1 <= value2))

    def eq(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(int(value1 == value2))

    def ge(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(int(value1 >= value2))

    def gt(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(int(value1 > value2))

    def ne(self):
        value1, value2 = self.stack.pop_int2()
        self.stack.push(int(value1 <> value2))

    def lt_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(int(value1 < value2))

    def le_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(int(value1 <= value2))

    def eq_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(int(value1 == value2))

    def ge_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(int(value1 >= value2))

    def gt_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(int(value1 > value2))

    def ne_f(self):
        value1, value2 = self.stack.pop_float2()
        self.stack.push(int(value1 <> value2))

    def is_inf(self):
        value = self.stack.pop_float()
        self.stack.push(int(math.isinf(value)))

    def is_nan(self):
        value = self.stack.pop_float()
        self.stack.push(int(math.isnan(value)))

    def to_float(self):
        value = self.stack.pop_int()
        self.stack.push(float(value))

    def to_int(self):
        value = self.stack.pop_float()
        self.stack.push(int(value))

    def make_label(self, line):
        m = re.match(r'\A([a-zA-Z]\w*):\Z', line)
        if m:
            label = m.group(1)
            if label in self.labels:
                raise DuplicateLabelError(label)
            self.labels[label] = len(self.memory)
            return True
        return False

    def make_op(self, line):
        fields = line.split()
        if DEBUG_OUTPUT: sys.stderr.write('op: %s' % (fields[0],))
        tup =  self.opcodes.get(fields[0], None)
        if DEBUG_OUTPUT: sys.stderr.write(' tup = %s\n' % (tup,))
        if not tup:
            return False
        if tup[0] != len(fields) - 1:
            return False
        if tup[0] == 0:
            self.memory.append(tup[1])
            return True
        self.memory.append(tup[1](*fields[1:]))
        return True

    def read(self):
        for line in fileinput.input():
            sys.stderr.write(line)
            line = line.strip()
            if line == '':
                continue
            if line.startswith('#'):
                # Comment.
                continue
            if self.make_label(line):
                continue
            if self.make_op(line):
                continue
            raise ParseError(line)
        return self

    def run(self, entry_point=0):
        if DEBUG_OUTPUT: sys.stderr.write('entry at %d\n' % (entry_point,))
        self.program_counter = entry_point
        while True:
            pc = self.program_counter
            self.program_counter += 1
            if DEBUG_OUTPUT:
                self.stack.dump()
                sys.stderr.write('%d: %s\n' % (pc, self.memory[pc],))
            self.memory[pc]()

    def help(self):
        for opname in sorted(self.opcodes.iterkeys()):
            _level, _func, stack, description = self.opcodes[opname]
            sys.stdout.write("%-16s\t%-32s\t%s\n" % (opname, stack, description))

if __name__ == '__main__':
    if len(sys.argv) > 1:
        if sys.argv[1] in ['--help', '-h']:
            VM().help()
            sys.exit(1)
    VM().read().run()
