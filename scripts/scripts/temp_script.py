#!/usr/bin/env python
# coding=utf-8

"""
    Just temporary script for some experiments.
"""

import xlwings as xw

wb = xw.Book('sample.xlsx')
sheet1 = wb.sheets['Sheet3']  # talk to sheet by name (active sheet if name doesn't exist)
sheet2 = wb.sheets[0]  # talk to sheet by specified index (active sheet if index doesn't exist)

sheet1.range('B1').value = 'Sheet300'
sheet1.range('B2').value = 24600
sheet1.range('B3').value = 250001

sheet2.range('B1').value = 'Sheet100'
sheet2.range('B2').value = 14600
sheet2.range('B3').value = 150001

for x in range(1, 10):
    sheet1.range('B' + str(x + 5)).value = 40 * x
    sheet2.range('B' + str(x + 5)).value = 10 * x

# save workbook after updating
wb.save()
