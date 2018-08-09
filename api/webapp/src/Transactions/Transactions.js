import React, { Component } from 'react';

export class Transactions extends Component {
    
    constructor(props) {
        super(props);
        this.state = { weeks: [], loading: true };
        this.getTransactions();
        this.getTransactions = this.getTransactions.bind(this);
        this.downloadTransactions = this.downloadTransactions.bind(this);
    }

    getTransactions() {

        var headers = new Headers();
        headers.append('Accept', 'application/json');
        headers.append('Authorization', 'Bearer ' + localStorage.getItem('access_token'));

        var getRequest = new Request('http://localhost:8080/api', {
            headers: headers
        });

        fetch(getRequest)
            .then(response => response.json())
            .then(data => {
                this.setState({ weeks: data, loading: false });
            });
    }

    downloadTransactions() {
        var headers = new Headers();
        headers.append('Accept', 'application/json');
        headers.append('Authorization', 'Bearer ' + localStorage.getItem('access_token'));

        var postRequest = new Request('http://localhost:8080/fetchTransactions', {
            method: "POST",
            headers: headers
        });

        fetch(postRequest)
            .then((() => {
               this.getTransactions();
            }));
        }

    static renderTransactions(weeks) {
        return (
            <div>
                {weeks.map(week => 
                    <div key={week.id}>
                    <h1>Week starting {week.startDate.toString()}</h1>
                 
                    {week.transactions.map(tran =>
                        <p key={tran.id}>
                        {tran.amount} on {tran.date} at {tran.location}
                        </p>
                    )}
                    </div>
                )}
                
            </div>
        )
    }

    render() {
        let contents =
            <div>
            <button onClick={this.downloadTransactions}>
                Download transaction history
            </button>
            <button onClick={this.getTransactions}>
                Show transaction history
            </button>
            {Transactions.renderTransactions(this.state.weeks)}
            </div>

        return (
            <div>
                <h1>Lunch by week</h1>
                {contents}
            </div>
        );
    }
}
