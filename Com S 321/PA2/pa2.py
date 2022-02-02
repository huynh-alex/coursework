#!/usr/bin/env python

def binary_instr_to_assembly(line):
    instr = ''
    instr_type = ''
    if line[0:11] == '10001011000':
        instr = 'ADD'
        instr_type = 'R'
    elif line[0:11] == '10001010000':
        instr = 'AND'
        instr_type = 'R'
    elif line[0:11] == '11010110000':
        instr = 'BR'
        instr_type = 'R'
    elif line[0:11] == '11001010000':
        instr = 'EOR'
        instr_type = 'R'
    elif line[0:11] == '11010011011':
        instr = 'LSL'
        instr_type = 'R'
    elif line[0:11] == '11010011010':
        instr = 'LSR'
        instr_type = 'R'
    elif line[0:11] == '10101010000':
        instr = 'ORR'
        instr_type = 'R'
    elif line[0:11] == '11001011000':
        instr = 'SUB'
        instr_type = 'R'
    elif line[0:11] == '11101011000':
        instr = 'SUBS'
        instr_type = 'R'
    elif line[0:11] == '10011011000':
        instr = 'MUL'
        instr_type = 'R'
    elif line[0:11] == '11111111101':
        instr = 'PRNT'
        instr_type = 'R'
    elif line[0:11] == '11111111100':
        instr = 'PRNL'
        instr_type = 'R'
    elif line[0:11] == '11111111110':
        instr = 'DUMP'
        instr_type = 'R'
    elif line[0:11] == '11111111111':
        instr = 'HALT'
        instr_type = 'R'
        
    elif line[0:10] == '1001000100':
        instr = 'ADDI'
        instr_type = 'I'
    elif line[0:10] == '1001001000':
        instr = 'ANDI'
        instr_type = 'I'
    elif line[0:10] == '1101001000':
        instr = 'EORI'
        instr_type = 'I'
    elif line[0:10] == '1011001000':
        instr = 'ORRI'
        instr_type = 'I'
    elif line[0:10] == '1101000100':
        instr = 'SUBI'
        instr_type = 'I'
    elif line[0:10] == '1111000100':
        instr = 'SUBIS'
        instr_type = 'I'
    
    elif line[0:11] == '11111000010':
        instr = 'LDUR'
        instr_type = 'D'
    elif line[0:11] == '11111000000':
        instr = 'STUR'
        instr_type = 'D'

    elif line[0:8] == '01010100':
        instr = 'B.cond'
        instr_type = 'CB'
    elif line[0:8] == '10110101':
        instr = 'CBNZ'
        instr_type = 'CB'
    elif line[0:8] == '10110100':
        instr = 'CBZ'
        instr_type = 'CB'

    elif line[0:6] == '000101':
        instr = 'B'
        instr_type = 'B'
    elif line[0:6] == '100101':
        instr = 'BL'
        instr_type = 'B'
    
    params = ''
    if(instr_type == 'R'):
        params = get_r_params(line[11:])
    if(instr_type == 'I'):
        params = get_i_params(line[10:])
    if(instr_type == 'D'):
        params = get_d_params(line[11:])
    if(instr_type == 'B'):
        params = get_b_params(line[6:])
    if(instr_type == 'CB'):
        if instr == 'B.cond':
            params = get_bcond_params(line[8:])
        else:
            params = get_cb_params(line[8:])
    line = make_line(instr, params)
    
    return line

#takes binary numbers and converts to decimal
def bin2dec(binary):
    binary = int(binary)
    decimal = 0
    power = 0
    while binary > 0:
        digit = binary % 10
        decimal = decimal + digit * pow(2, power)
        binary //= 10
        power += 1
    return decimal

#takes decimal numbers and converts to an 8-bit binary
def dec2bin(decimal):
    return format(decimal,'08b')

#takes a a binary string and returns the signed decimal value; code adapted from https://stackoverflow.com/a/9147327
def twos_complement(binary_string):
    width = len(binary_string)
    binary_value = int(binary_string,2)
    if (binary_value & (1 << (width - 1))) != 0:
        binary_value = binary_value - (1 << width)
    decimal_value = binary_value
    return decimal_value

def get_r_params(line):
    rm = bin2dec(line[0:5])
    shamt = bin2dec(line[5:11])
    rn = bin2dec(line[11:16])
    rd = bin2dec(line[16:])
    return {'rm': rm, 'shamt': shamt, 'rn': rn, 'rd': rd}

def get_i_params(line):
    alu_imm = line[0:12]
    alu_imm = twos_complement(alu_imm)
    rn = bin2dec(line[12:17])
    rd = bin2dec(line[17:])
    return {'alu_imm': alu_imm, 'rn': rn, 'rd': rd}

def get_d_params(line):
    dt_addr = line[0:9]
    dt_addr = twos_complement(dt_addr)
    op = bin2dec(line[9:11])
    rn = bin2dec(line[11:16])
    rt = bin2dec(line[16:])
    return {'dt_addr': dt_addr, 'op': op, 'rn': rn, 'rt': rt}

def get_b_params(line):
    br_addr = 0
    br_addr = line[0:]
    br_addr = twos_complement(br_addr)
    return {'br_addr': br_addr}

def get_cb_params(line):
    cond_br_addr = 0
    cond_br_addr = line[0:19]
    cond_br_addr = twos_complement(cond_br_addr)
    rt = bin2dec(line[19:])
    return {'cond_br_addr': cond_br_addr, 'rt': rt}

def get_bcond_params(line):
    cond_br_addr = line[0:19]
    cond = bin2dec(line[19:])
    rt = ''
    if cond == 0:
        rt = 'EQ'
    elif cond == 1:
        rt = 'NE'
    elif cond == 2:
        rt = 'HS'
    elif cond == 3:
        rt = 'LO'
    elif cond == 4:
        rt = 'MI'
    elif cond == 5:
        rt = 'PL'
    elif cond == 6:
        rt = 'VS'
    elif cond == 7:
        rt = 'VC'
    elif cond == 8:
        rt = 'HI'
    elif cond == 9:
        rt = 'LS'
    elif cond == 10:
        rt = 'GE'
    elif cond == 11:
        rt = 'LT'
    elif cond == 12:
        rt = 'GT'
    elif cond == 13:
        rt = 'LE'
    cond_br_addr = twos_complement(cond_br_addr)
    
    return {'cond_br_addr': cond_br_addr, 'rt': rt}

"""
params:
 - instr: the name of the instruction
 - p: parameters of that instruction
"""
def make_line(instr, p):
    line = ''
    if instr == 'ADD' or instr == 'AND' or instr == 'EOR' or instr == 'ORR' or instr == 'SUB' or instr == 'SUBS' or instr == 'MUL':
        line = instr + ' X' + str(p['rd']) + ', ' + 'X' + str(p['rn']) + ', ' + 'X' + str(p['rm'])
    elif instr == 'LSL' or instr == 'LSR':
        line = instr + ' X' + str(p['rd']) + ', ' + 'X' + str(p['rn']) + ', ' + '#' + str(p['shamt'])
    elif instr == 'ADDI' or instr == 'ANDI' or instr == 'EORI' or instr == 'ORRI' or instr == 'SUBI' or instr == 'SUBIS':
        line = instr + ' X' + str(p['rd']) + ', ' + 'X' + str(p['rn']) + ', ' + '#' + str(p['alu_imm'])
    elif instr == 'PRNT':
        line = instr + ' X' + str(p['rd'])
    elif instr == 'PRNL' or instr == 'DUMP' or instr == 'HALT':
        line = instr
    elif instr == 'LDUR' or instr == 'STUR':
        line = instr + ' X' + str(p['rt']) + ', ' + '[X' + str(p['rn']) + ', ' + '#' + str(p['dt_addr']) + ']'
    elif instr == 'CBNZ' or instr == 'CBZ':
        line = instr + ' X' + str(p['rt']) + ', ' + str(p['cond_br_addr'])
    elif instr == 'B.cond':
        line = 'B.' + p['rt'] + ' ' + str(p['cond_br_addr'])
    elif instr == 'B' or instr == 'BL':
        line = instr + ' ' + str(p['br_addr'])
    elif instr == 'BR':
        line = instr + ' X' + str(p['rn'])
        
    return line

def find_branch_type(instr):
    index = 0
    # find up until the last space or negative sign; up to here is the branch type
    for i in range(len(instr)-1, 0, -1):
        if not instr[i:].lstrip('-').isnumeric():
            index = i
            break
    return instr[:index+1]

def find_label_jump_amount(branch_type, binary_instr):
    binary_value = 0
    # the jump amount depends on whether this is B/BL, or some other branch
    if(branch_type.strip() == 'B' or branch_type.strip() == 'BL'):
        binary_value = binary_instr[6:]
    else:
        binary_value = binary_instr[8:27]
    decimal_val = twos_complement(binary_value)
    return decimal_val

def main(input_file):
    
    code = '' # this will be the entire binary encoding of the legv8asm file
    with open(input_file, 'rb') as f: # open the file in reading-binary mode
        entire_code = f.read()   # binary format
        entire_code_list = list(entire_code) # make a list where each index is 1 byte at a time; each index holds a decimal number now
        for i in range(len(entire_code_list)):
            code += dec2bin(entire_code_list[i]) # add 1 byte at a time, each time adding in binary format

    """NO FILE OUTPUT"""
    # output_file = open('a.legv8asm', 'w')  # create file
    # output_file = open('a.legv8asm', 'r+') # writing mode for the file
    line_num = 0
    labels = {}
    # pre-initialize each line to be an empty string; there are len(code) // 32 lines
    # however we double it as we consider the worst case where every line has a label
    lines = ['' for i in range(2 * (len(code)) // 32)]
    
    for i in range(0, len(code), 32):
        binary_instr = code[i:i+32]
        instr = binary_instr_to_assembly(binary_instr)
        
        # handle placement of branch instructions into our list
        if instr[0:2] == 'B ' or instr[0:3] == 'BL ' or instr[0:4] == 'CBZ ' or instr[0:5] == 'CBNZ ' or instr[0:2] == 'B.':
            branch_type = find_branch_type(instr) # we manually insert the instruction because it must go with its label
            jump_amount = find_label_jump_amount(branch_type, binary_instr) # we find the jump amount
            label_line = line_num + jump_amount # the line number where label should be
            if str(label_line) not in labels:
                labels[str(label_line)] = label_line
                # place the label
                lines[label_line] = ('LABEL_LINE_' + str(label_line) + ':') + ' ' + lines[label_line]
            # place the branch instruction
            lines[line_num] += (branch_type + 'LABEL_LINE_' + str(labels[str(label_line)]))
        
        # this is a non-branch instruction
        else:
            # place the instruction if the line is empty
            if lines[line_num] == '':
                lines[line_num] = instr
            # the line is not empty (there is a label here), so place the instruction after the label
            else:
                lines[line_num] = lines[line_num] + ' ' + instr
        line_num += 1
        
    for i, line in enumerate(lines):
        if(line != ''):
            print(line)
            """NO FILE OUTPUT"""
            # output_file.write(line)
            # if lines[i+1] != '': # only add a new line if it is not the last instruction
                # output_file.write("\n");

import sys

input_file = sys.argv[1]
main(input_file)