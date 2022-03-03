# To add a new cell, type '# %%'
# To add a new markdown cell, type '# %% [markdown]'
# %% [markdown]
# # Preparation

# %%
import pandas as pd
import numpy as np
import matplotlib as mpl
import matplotlib.pyplot as plt
import tushare as ts
import operator


# %%
# functions set
def format_code(df):
    global df_element
    code_list = list(df_element['code'])
    exchange_list = list(df_element['exchange'])
    for i in range(len(code_list)):
        if exchange_list[i] == "SHH":
            code_list[i] = "{:0>6d}.SH".format(code_list[i])
        else:
            code_list[i] = "{:0>6d}.SZ".format(code_list[i])
    return code_list


# %%
# my fundamental
class account:
    def __init__(self):
        # initialize for tushare
        ts.set_token("b56903a55b7f98b8ce739e6eff853f726d6add955874aecffeb3995f")
        self.pro = ts.pro_api()
        
        # information about account
        self.origin_fund = 10000000
        self.reserve_fund_proportion = 0.3
        self.stocks = [] # code : number, unit of number : hand = 100 stock
        self.cash = 10000000 # unit : RMB
        self.stocks_value = 0
        self.future_value = 0
        self.net_worth = []

    def cal_net_worth(self,date):
        self.cal_stocks_value(date)
        self.net_worth.append((self.cash + self.stock_value + self.future_value)/self.origin_fund)

    def cal_stocks_value(self,date):
        code_list = list(self.stocks.keys())
        price_list = [self.pro.daily(st_code = code, start_date = date, end_date = date)['close'][0] for code in code_list]
        num_list = list(self.stocks.values())
        self.stocks_value = sum(map(operator.mul,price_list,num_list)) * 100
    
    # using a series of proportions of cash to buy a series of stocks, the remaining fund return to cash
    def buy(self,date,code_list,proportion_list):
        price_list = [list(self.pro.daily(st_code = code, start_date = date, end_date = date)['close'])[0] for code in code_list]
        for code,proportion,price in zip(code_list,proportion_list,price_list):
            budget = (1 -reserve_fund_proportion) * cash * proportion
            num = int(budget/price)
            if code in list(self.stocks.keys()):
                self.stocks[code] += num
            else :
                self.stocks[code] = num
            cost = num * price
            self.cash -= cost

    # when selling a certain stock, sell them all
    def sell(self,date,code_list):
        price_list = [self.pro.daily(st_code = code, start_date = date, end_date = date)['close'][0] for code in code_list]
        for code,price in zip(code_list,price_list):
            self.cash += self.stocks[code] * price
            self.stocks.pop(code)


# %%
# to get a list of trade date in a year
ts.set_token("b56903a55b7f98b8ce739e6eff853f726d6add955874aecffeb3995f")
pro = ts.pro_api()
medium = pro.daily(ts_code = '600000.SH', start_date = '20200101', end_date = '20210101')
trade_date_list = list(medium['trade_date'])[::-1]


# %%
# get some basic information
df_element = pd.read_excel(r'Data/沪深300成分股-2020.xls')
name_list = df_element['name']
code_list = df_element['code']
exchange_list = df_element['exchange']


# %%
# get a list of date and index of CSI300
df_hs300_data = pd.read_excel(r'Data/hs300_data_origin.xlsx')
date = pd.to_datetime(df_hs300_data['Date'],unit = 'd', origin = '1899-12-30')
# there are 243 days when stock market run
date = date[:243]
close_price = df_hs300_data['closing price'][:243] # 指数


# %%
# plot the net worth graph of CSI300
fig = plt.figure(figsize=(12.8,9.6),dpi = 200,facecolor='w')
# plt.plot(date,index)
ax = fig.add_subplot(221,alpha = 0.5)
ax.plot(date,close_price)


# %%



# %%
# deal with the format of data
df_10_year_element = pd.read_excel(r'Data/hs300历年成分.xlsx')
df_10_year_element['c10'] = format_code(df_element)
df_element['code'] = format_code(df_element)
panel = list(df_10_year_element['c10'])
print("df element",df_element)
print("10 year, 2020",df_10_year_element['c10'])
print("10 year",df_10_year_element)


# %%
# sort out stocks that haven't been died out
for i in panel:
    for j in range(10):
        if i not in list(df_10_year_element['c{}'.format(j+1)]):
            panel.remove(i)
            break


# %%
# finally get the name of those stocks exist more than 10 years and their corresponding code
print("number : ",len(panel))
# get their names
panel_name = []
panel_dict = {}
for i in range(300):
    if list(df_element['code'])[i] in panel:
        panel_dict['{}'.format(list(df_element['code'])[i])] = df_element['name'][i]
print(panel_dict)


# %%
full_stock_data = []
for p in panel:
    full_stock_data.append(pro.daily(ts_code=p, start_date='20200101', end_date='20201231'))


# %%
test_data = pro.daily(ts_code = '600000.SH',start_date = '20200102', end_date = '20210101')
# test_data['date']
print(list(test_data['trade_date'])[::-1])


# %%


# %% [markdown]
# # Part I random sample

# %%
randpick_n = 20
# return a list of code
def random_pick(panel_dict, randpick_n):
    code_list = list(panel_dict.keys())
    return np.random.choice(code_list,randpick_n)
stocks = random_pick(panel_dict,randpick_n)


# %%
me = account()
me.buy('20200101', stocks,[1/20 for i in range(len(stocks))])


