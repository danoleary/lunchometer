import React from 'react';

export class Transaction extends React.Component {
    render() {
        return (
            <article class="tile is-child notification has-background-grey-lighter" >
                <p class="subtitle">
                    {this.props.tran.day}: £{this.props.tran.amount} at {this.props.tran.location}
                </p>
            </article>
        )
    }
}