const functions = require("firebase-functions");
const admin = require("firebase-admin");
admin.initializeApp();
exports.sendEventReminders = functions.firestore.document('events/{eventID}')
    .onCreate((snapshot, context) => {
        const eventData = snapshot.data();
        const eventId = context.params.eventID;

        // Get the time of the event and calculate the times for the reminders
        const eventTime = new Date(`${eventData.eventStartDate} ${eventData.timeBeginning}`);
        const oneDayBefore = new Date(eventTime.getTime() - (24 * 60 * 60 * 1000));
        const twoHoursBefore = new Date(eventTime.getTime() - (2 * 60 * 60 * 1000));
        console.log(eventTime.toLocaleString('en-US',{ year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }))
        const message = (time, body) => ({
            topic: `event_${eventId}`,
            data:{
                scheduledTime: time.toLocaleString('en-US',{ year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }),
            },
            notification: {
                title: `Upcoming event: ${eventData.title}`,
                body
            }
        });
        console.log('Creating')

        admin.messaging().send(message(oneDayBefore, `Event "${eventData.title}" is happening tomorrow`))
            .then(response => console.log(`Successfully sent message: ${response}`))
            .catch(error => console.error(`Error sending message: ${error}`));

        admin.messaging().send(message(twoHoursBefore, `Event "${eventData.title}" is happening in 2 hours`))
            .then(response => console.log(`Successfully sent message: ${response}`))
            .catch(error => console.error(`Error sending message: ${error}`));

        admin.messaging().send(message(eventTime, `Event "${eventData.title}" is happening now`))
            .then(response => console.log(`Successfully sent message: ${response}`))
            .catch(error => console.error(`Error sending message: ${error}`));
    });
exports.sendEventRemindersUpdate = functions.firestore.document('events/{eventID}')
    .onUpdate((change, context) => {
        const eventData = change.after.data();
        const eventId = context.params.eventID;

        // Get the time of the event and calculate the times for the reminders
        const eventTime = new Date(`${eventData.eventStartDate} ${eventData.timeBeginning}`);
        const oneDayBefore = new Date(eventTime.getTime() - (24 * 60 * 60 * 1000));
        const twoHoursBefore = new Date(eventTime.getTime() - (2 * 60 * 60 * 1000));
        console.log(eventTime.toLocaleString('en-US',{ year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }))
        const message = (time, body) => ({
            topic: `event_${eventId}`,
            data:{
                scheduledTime: time.toLocaleString('en-US',{ year: 'numeric', month: '2-digit', day: '2-digit', hour: '2-digit', minute: '2-digit', hour12: false }),
            },
            notification: {
                title: `Upcoming event: ${eventData.title}`,
                body
            }
        });
        console.log('Updating')

        admin.messaging().send(message(oneDayBefore, `Event "${eventData.title}" is happening tomorrow`))
            .then(response => console.log(`Successfully sent message: ${response}`))
            .catch(error => console.error(`Error sending message: ${error}`));

        admin.messaging().send(message(twoHoursBefore, `Event "${eventData.title}" is happening in 2 hours`))
            .then(response => console.log(`Successfully sent message: ${response}`))
            .catch(error => console.error(`Error sending message: ${error}`));

        admin.messaging().send(message(eventTime, `Event "${eventData.title}" is happening now`))
            .then(response => console.log(`Successfully sent message: ${response}`))
            .catch(error => console.error(`Error sending message: ${error}`));
    });

