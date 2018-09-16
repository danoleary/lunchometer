import React from 'react';
import { Transaction } from './transaction';

export class Week extends React.Component {
    render() {
        const week = this.props.week;
        return (
            <div
                class="column is-one-third is-primary"
                onClick={() => this.props.onClick(week.id)}>
                <article class="tile is-child notification is-primary">
                    <p class="title">
                        Mon-Thu avg: £{week.averageMonToThurs}
                    </p>
                    <p class="subtitle">Friday spend: {week.fridaySpend}</p>
                    <p class="subtitle">Week commencing {week.startDate}</p>
                </article>
                {
                    this.props.showTransactions
                        .filter(x => x.id === week.id)[0]
                        .showDetail && week.transactions.map(t => <Transaction tran={t} /> )
                }
            </div>
        )
    }
}