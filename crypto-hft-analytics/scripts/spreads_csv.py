import plac
import torch
import pandas as pd
import os
import datetime as dt

def load_spreads_tensors(spreads_pt):
    spreads_tensor = list(torch.jit.load(spreads_pt).parameters())[0]
    df = pd.DataFrame({
        'Time_utc': spreads_tensor[0].long().numpy()*60,
        'BidAskSpread_bp': 1e4*spreads_tensor[1].numpy(),
        'MidPrice_Open': spreads_tensor[2].numpy(),
        'MidPrice_High': spreads_tensor[3].numpy(),
        'MidPrice_Low': spreads_tensor[4].numpy(),
        'MidPrice_Close': spreads_tensor[5].numpy()
    })
    df['Time_utc'] = df.Time_utc.map(dt.datetime.utcfromtimestamp)
    return df



@plac.annotations(
    data=('Path to input data folder', 'option', 'd', str),
    out=('Path to output data folder', 'option', 'o', str)
)
def main(data, out):
    data_dir = 'data' if data is None else data
    out_dir = data_dir if out is None else out
    for spreads_pt in os.listdir(data_dir):
        if spreads_pt[-3:]=='.pt':
            spreads = load_spreads_tensors(os.path.join(data_dir, spreads_pt))
            print(f'{spreads_pt[:-3]} contains {spreads.index.size} entities')
            spreads.to_csv(os.path.join(out_dir, f'{spreads_pt[:-3]}.csv'), index=False)


if __name__ == '__main__':
    plac.call(main)